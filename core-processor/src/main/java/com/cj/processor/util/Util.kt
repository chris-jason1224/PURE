package com.cj.processor.util
import org.jetbrains.annotations.NotNull
import java.io.File
import javax.annotation.processing.Messager
import javax.tools.Diagnostic
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

/**
 * Author:chris - jason
 * Date:2020-01-17.
 * Package:com.cj.processor.util
 */


//输出错误日志
fun log_e(@NotNull messager: Messager, msg: String) {
    messager.printMessage(Diagnostic.Kind.WARNING, msg)
}


/**
 * 通过moduleName解析包名
 */
fun getPackageName(moduleName: String? = ""): String {

    var manifest = File("$moduleName/src/main/AndroidManifest.xml")

    //manifest不存在
    if (!manifest.exists()) {
        //log_e(messager,"${moduleName}中manifest文件不存在")
        return ""
    }

    var factory = SAXParserFactory.newInstance()

    try {
        var saxParser: SAXParser = factory.newSAXParser()
        var handler = PackageParseHandler()
        saxParser.parse(manifest, handler)
        return handler.packageName
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}