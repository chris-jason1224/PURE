package com.cj.processor.bus

import com.cj.processor.util.getPackageName
import com.cj.processor.util.log_e
import com.google.gson.Gson
import com.google.gson.stream.JsonWriter
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.cj.annotation.MODULE_EVENT_FILE_PRE
import com.cj.annotation.OPTION_MODULE_NAME
import com.cj.annotation.bus.EventRegister
import com.cj.annotation.bus.ModuleEventCenter
import com.cj.annotation.bus.model.EventRegisterEntity
import com.cj.annotation.bus.model.ModuleEventCenterEntity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.lang.StringBuilder
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement


/**
 * Author:chris - jason
 * Date:2019-12-25.
 * Package:com.cj.processor.bus
 * 组件间消息总线注解处理器
 *
./gradlew --no-daemon -Dorg.gradle.debug=true -Dkotlin.daemon.jvm.options="-Xdebug,-Xrunjdwp:transport=dt_socket\,address=5005\,server=y\,suspend=n" :clean assemble
kapt.kotlin.generated -> /Users/mayikang/github/Template-KT/biz_login/build/generated/source/kaptKotlin/debug
 */
class ModuleBusProcessor : AbstractProcessor() {

    private lateinit var messager: Messager
    private var options: MutableMap<String, String>? = null
    private var mFiler: Filer? = null

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        messager = processingEnv!!.messager
        options = processingEnv.options
        mFiler = processingEnv.filer
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf<String>(ModuleEventCenter::class.java.canonicalName)
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        //获取参数
        var moduleName = options!![OPTION_MODULE_NAME]
        var generatedDir = options!!["kapt.kotlin.generated"]
        //mac os:kapt.kotlin.generated -> /Users/mayikang/github/Template-KT/biz_login/build/generated/source/kaptKotlin/debug
        //windows:kapt.kotlin.generated -> E:\GH\PURE\app\build\generated\source\kaptKotlin\debug
        val elements = roundEnv!!.getElementsAnnotatedWith(ModuleEventCenter::class.java)

        //未使用ModuleEventCenter注解
        if (elements.size == 0) {
            log_e(messager, "@${ModuleEventCenter::class.java.name}注解在${moduleName}中未使用")
            return false
        }

        if (elements.size > 1) {
            log_e(
                messager,
                "@${ModuleEventCenter::class.java.name}注解在${moduleName}中使用了多次，请去除重复注解，保留一个即可"
            )
            return false
        }

        for (element in elements) {

            if (element.kind != ElementKind.CLASS) {
                log_e(messager, "@${ModuleEventCenter::class.java.name}注解只能作用于类上")
                return false
            }

            val rootElements = roundEnv.rootElements

            /**获取@ModuleEventCenter注解的类名**/
            var qualifiedName = ""
            for (e in rootElements) {
                val annotation: ModuleEventCenter? = e.getAnnotation(ModuleEventCenter::class.java)
                val str = (e as TypeElement).qualifiedName.toString()
                if (annotation != null && !str.isBlank()) {
                    qualifiedName = str
                    break
                }
            }
            if (qualifiedName.isBlank()) {
                log_e(messager, "无法获取类名")
                return false
            }

            var moduleEventCenterEntity =
                ModuleEventCenterEntity(moduleName ?: "", qualifiedName, getPackageName(moduleName))

            /**获取@ModuleEventCenter注解的类中的属性字段**/
            var enclosedElements = element.enclosedElements
            //保存字段的集合，Class类型在编译期间无法获得
            var methodList: MutableList<EventRegisterEntity> = mutableListOf()
            for (ele in enclosedElements) {
                if (ele.kind == ElementKind.FIELD) {
                    //添加@EventRegister注解的字段的镜像
                    //var typeMirror: TypeMirror = ele.asType()
                    //字段的类型
                    //var typeKind = typeMirror.kind
                    //字段的名称
                    var fieldName = ele.simpleName
                    var annotationMirrors = ele.annotationMirrors
                    if (annotationMirrors != null) {
                        for (mirror in annotationMirrors) {
                            var annotationType = mirror.annotationType.toString()
                            if (annotationType == EventRegister::class.java.canonicalName) {

                                var className: String = ""
                                var asList: Boolean = false

                                //key被定义成方法 EventRegister.type()
                                for (entry in mirror.elementValues.entries) {

                                    if (entry.key.simpleName.toString() == "classType") {
                                        className = entry.value.value.toString()
                                    }

                                    if (entry.key.simpleName.toString() == "asList") {
                                        asList = entry.value.value.toString().toBoolean()
                                    }

                                }

                                var method: EventRegisterEntity = EventRegisterEntity(
                                    fieldName = fieldName.toString(),
                                    className = className,
                                    asList = asList
                                )
                                methodList.add(method)
                            }
                        }
                    }
                }
            }

            /**通过JavaPoet按规则生成对应的接口**/
            //kotlin命名不能包含@/$/#等特殊字符
            //接口名：Gen_ + moduleName + _Interface
            //方法名：Gen_ +fieldName + _Method ,无参数,返回类型 com.cj.base_common.bus.Observable<className>

            if (methodList.size == 0) {
                log_e(messager, "${moduleName}暂无组件消息注册")
                return false
            }

            var funSpecs: MutableList<FunSpec> = mutableListOf()
            for (eventRegisterEntity in methodList) {
                // 生成方法
                try {
                    var names = eventRegisterEntity.className.split(".")
                    var packageName = ""
                    var simpleName = ""
                    //@EventRegister可能会添加一些系统类，无法直接通过反射获取Type
                    when (names.size) {
                        1 -> {
                            packageName = "kotlin"
                            simpleName = names[0]
                        }
                        2 -> {
                            packageName = names[0]
                            simpleName = names[1]
                        }
                        else -> {
                            var buffer = StringBuffer()
                            for (i in 0..names.size - 2) {
                                buffer.append(".")
                                buffer.append(names[i])
                            }
                            packageName = buffer.toString().replaceFirst(".", "")
                            simpleName = names[names.size - 1]
                        }
                    }

                    //泛型类型
                    var type = when ("${packageName}.${simpleName}") {
                        //java基础数据类型转换
                        "java.lang.String", "kotlin.string" -> ClassName("kotlin", "String")
                        "java.lang.Integer", "kotlin.int" -> ClassName("kotlin", "Int")
                        "java.lang.Long", "kotlin.long" -> ClassName("kotlin", "Long")
                        "java.lang.Double", "kotlin.double" -> ClassName("kotlin", "Double")
                        "java.lang.Float", "kotlin.float" -> ClassName("kotlin", "Float")
                        "java.lang.Short", "kotlin.short" -> ClassName("kotlin", "Short")
                        "java.lang.Boolean", "kotlin.boolean" -> ClassName("kotlin", "Boolean")
                        "java.lang.Byte", "kotlin.byte" -> ClassName("kotlin", "Byte")
                        "java.lang.Object", "kotlin.Any" -> ClassName("kotlin", "Any")

                        //自定义的类型
                        else -> ClassName(packageName, simpleName)
                    }

                    //返回值类型：Observable<type>
                    var observable =
                        ClassName("com.cj.foundation.bus", "Observable").parameterizedBy(type)

                    //返回值类型：Observable<List<type>>
                    if(eventRegisterEntity.asList){
                        observable=ClassName("com.cj.foundation.bus","Observable").parameterizedBy(ClassName("kotlin.collections","MutableList").parameterizedBy(ClassName(type.packageName,type.simpleName)))
                    }

                    var funSpec: FunSpec =
                        FunSpec.builder(eventRegisterEntity.fieldName)
                            .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
                            .addKdoc("@param asList = ${eventRegisterEntity.asList}\n@param T = ${type.canonicalName}") // 添加注释
                            .returns(observable)
                            .build()

                    funSpecs.add(funSpec)

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            // interfaceBuilder方法标志生成接口类
            var typeSpec: TypeSpec = TypeSpec.interfaceBuilder("Gen_" + moduleName + "_Interface")
                .addModifiers(KModifier.PUBLIC)
                .addKdoc("Auto generate code ,do not edit")
                .addFunctions(funSpecs)
                .build()

            // 写入文件
            var ktFile = FileSpec.builder("com.cj.bus", typeSpec.name!!)
                .addType(typeSpec)
                .addComment("powered by kotlinPoet")
                .build()
            try {
                //可以直接生成到java源代码文件或者生成到build目录下
                //var f = File("base_common/src/main/java/")
                //kapt.kotlin.generated -> /Users/mayikang/github/Template-KT/biz_login/build/generated/source/kaptKotlin/debug
                //将生成的接口文件全部替换到 /Users/mayikang/github/Template-KT/base_common/build/generated/source/kapt/debug 路径下

                //不同的电脑和项目下，该路径不同，请自行修改避免报错
                var dirArray =
                    generatedDir!!.trim().replaceFirst(File.separator, "").split(File.separator)
                        .toMutableList()


                //dirArray[dirArray.size - 2] = "kapt"
                for ((index, varPath) in dirArray.withIndex()) {
                    if (0 == index) {
                        dirArray[0] = dirArray[0].replace(":", ":\\")
                    }
                    if (varPath == "build") {
                        dirArray[index - 1] = "base-foundation"
                    }
                }
                //windows下路径有盘符


                var builder = StringBuilder()
                for (d in dirArray) {
                    builder.append("${d}${File.separator}")
                }
                var finalPath: String = builder.toString()
                //var finalPath:String = "E:\\GH\\PURE\\base-foundation\\build\\generated\\source\\kaptKotlin\\debug"

                if (!finalPath.isBlank()) {
                    ktFile.writeTo(File(finalPath))
                }
                //生成配置json文件
                writeJsonToAssets(moduleEventCenterEntity)

            } catch (e: IOException) {
                e.printStackTrace()
            }
            return true
        }

        return false
    }


    private fun writeJsonToAssets(moduleEntity: ModuleEventCenterEntity) {
        var dir = File(moduleEntity.moduleName + "/src/main/assets")
        if (!dir.exists()) {
            dir.mkdirs()
        }

        var file = File(dir, MODULE_EVENT_FILE_PRE + moduleEntity.moduleName + ".json")
        try {
            if (file.exists()) {
                file.delete()
            }

            file.createNewFile()

            var gson = Gson()
            var out = FileOutputStream(file)
            var writer = JsonWriter(OutputStreamWriter(out, "UTF-8"))
            gson.toJson(moduleEntity, ModuleEventCenterEntity::class.java, writer)
            writer.flush()
            writer.close()

        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

}