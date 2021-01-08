package com.cj.processor.module
import com.cj.processor.util.getPackageName
import com.cj.processor.util.log_e
import com.google.gson.Gson
import com.google.gson.stream.JsonWriter
import com.cj.annotation.MODULE_INFO_PRE
import com.cj.annotation.OPTION_MODULE_NAME
import com.cj.annotation.module.ModuleRegister
import com.cj.annotation.module.model.ModuleInfoEntity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.Exception
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

/**
 * Author:chris - jason
 * Date:2020-01-17.
 * Package:com.cj.processor.module
 * 组件注册注解处理器
 */
class ModuleRegisterProcessor : AbstractProcessor() {

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
        return mutableSetOf<String>(ModuleRegister::class.java.canonicalName)
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {

        var moduleName = options!![OPTION_MODULE_NAME]

        if (moduleName == null || moduleName.isEmpty()) {
            return false
        }

        val elements = roundEnv!!.getElementsAnnotatedWith(ModuleRegister::class.java)


        //未使用ModuleRegister注解
        if (elements.size == 0) {
            log_e(messager, "@${ModuleRegister::class.java.name}注解在${moduleName}中未使用")
            return false
        }

        if (elements.size > 1) {
            log_e(
                messager,
                "@${ModuleRegister::class.java.name}注解在${moduleName}中使用了多次，请去除重复注解，保留一个即可"
            )
            return false
        }

        for (element in elements) {

            if (element.kind != ElementKind.CLASS) {
                log_e(messager, "@${ModuleRegister::class.java.name}注解只能作用于类上")
                return false
            }

            val rootElements = roundEnv.rootElements

            /**获取@ModuleRegister注解的类名**/
            var qualifiedName = ""
            for (e in rootElements) {
                val annotation: ModuleRegister? = e.getAnnotation(ModuleRegister::class.java)
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

            var moduleInfoEntity = ModuleInfoEntity(moduleName, getPackageName(moduleName), qualifiedName)

            writeJsonToAssets(moduleInfoEntity)

            return true
        }

        return false

    }

    private fun writeJsonToAssets(moduleInfoEntity: ModuleInfoEntity) {

        var dir = File("${moduleInfoEntity.moduleName}/src/main/assets")

        if (!dir.exists()) {
            dir.mkdirs()
        }

        var file = File(dir, "${MODULE_INFO_PRE}${moduleInfoEntity.moduleName}.json")

        try {
            if (file.exists()) {
                file.delete()
            }

            file.createNewFile()

            var gson = Gson()
            var out = FileOutputStream(file)
            var writer = JsonWriter(OutputStreamWriter(out,"UTF-8"))
            gson.toJson(moduleInfoEntity,ModuleInfoEntity::class.java,writer)
            writer.flush()
            writer.close()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


}