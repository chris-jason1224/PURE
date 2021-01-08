package com.cj.foundation.module

import android.util.Log
import com.cj.annotation.MODULE_INFO_PRE
import com.cj.annotation.module.model.ModuleInfoEntity
import com.cj.foundation.base.BaseApp
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.text.ParseException

/**
 * Author:chris - jason
 * Date:2020-01-17.
 * Package:com.cj.base_common.module
 */
object ModuleManager {

    private const val TAG = "ModuleManager"
    var moduleInfoList: MutableList<ModuleInfoEntity>? = null
    var delegateNameList: MutableList<String>? = null
    var delegateList: MutableList<IModuleDelegate>? = null


    init {
        moduleInfoList = mutableListOf()
        delegateNameList = mutableListOf()
        delegateList = mutableListOf()
    }

    //加载所有module信息
    fun loadModules() {
        var context = BaseApp.getInstance()

        try {
            var assetManager = context.resources.assets
            var fileList = assetManager.list("")
            for (file in fileList!!) {
                if (file.startsWith(MODULE_INFO_PRE) && file.endsWith(".json")) {

                    //解析json配置文件
                    var moduleInfo = parse(assetManager.open(file))
                    if (moduleInfo != null) {
                        moduleInfoList!!.add(moduleInfo)
                        delegateNameList!!.add(moduleInfo.delegateName)
                    }
                }
            }

            //ModuleInfo对应写到json描述文件的结构
            delegateList!!.addAll(
                getObjectsWithClassName(IModuleDelegate::class.java,
                    delegateNameList
                )
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun parse(inputStream: InputStream): ModuleInfoEntity? {

        var fromJson: ModuleInfoEntity? = null

        try {
            var isr = InputStreamReader(inputStream, "UTF-8")
            var br = BufferedReader(isr)

            var line: String = ""
            while (true) {
                line = br.readLine() ?: break
            }

            br.close()
            isr.close()

            var jsonObject = JSONObject(line)

            var moduleInfo = ModuleInfoEntity(
                jsonObject.optString("moduleName"),
                jsonObject.optString("packageName"),
                jsonObject.optString("delegateName")
            )
            return moduleInfo
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "解析Module.json出错")
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e(TAG, "解析Module.json出错")
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e(TAG, "解析Module.json出错")
        }
        return fromJson
    }

    private fun <T> getObjectsWithClassName(
        clazz: Class<T>,
        classNameList: List<String>?
    ): MutableList<T> {
        var objectList = ArrayList<T>()

        try {
            for (className in classNameList!!) {
                var aClass = Class.forName(className)
                //判断clazz对应的类 是否是 aClass对应的类的父类或者接口
                //aClass不能是接口
                if (clazz.isAssignableFrom(aClass) && clazz != aClass && !aClass.isInterface) {
                    //这里逻辑上单例，实例化一次clazz对应类的实例
                    objectList.add(Class.forName(className).getConstructor().newInstance() as T)
                }
            }

            if (objectList.size == 0) {
                Log.e(TAG, "No files were found, check your configuration please!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return objectList
    }


}