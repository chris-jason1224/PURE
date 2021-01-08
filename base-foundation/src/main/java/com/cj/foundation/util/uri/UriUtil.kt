package com.cj.foundation.util.uri

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import android.util.Base64
import com.cj.foundation.log.LOG
import java.io.*

object UriUtil {

    /**
     * uri转换成绝对地址
     */
    fun uri2File(context: Context, uri: Uri?): File? {

        if (uri == null || TextUtils.isEmpty(uri.path)) {
            return null
        }
        var file: File? = File(uri.path)
        if (file != null && file.exists() && file.isFile) {
            return file
        }
        file =
            UriToFilePath.uri2File(context, uri)
        if (file != null && file.exists() && file.isFile) {
            return file
        }

        //华为系统文件夹中目录转换
        if (UriToFilePath.isHuaweiUri(uri) && !TextUtils.isEmpty(uri.path)) {
            val rootPre = File.separator + "root"
            val path = if (uri.path!!.startsWith(rootPre)) uri.path!!
                .replace(rootPre, "") else uri.path!!
            file = File(path)
            if (file != null && file.exists() && file.isFile) {
                return file
            }
        }

        //所有转策略均失败，复制一份文件
        val res: String? = copyFile2UserTmpDir(context, uri)
        file = File(res)
        if(!TextUtils.isEmpty(res)  && file!=null && file.exists()){
            return file
        }

        return null
    }

    private fun copyFile2UserTmpDir(context: Context, uri: Uri?): String? {
        /** 复制文件 不能直接返回file对象，会导致一直持有file对象，即使文件复制成功，但是磁盘上还是没有生成对应的文件 */

        LOG.getInstance().e("start copy file to 外部存储目录目录")
        if (uri == null || TextUtils.isEmpty(uri.path)) {
            return ""
        }
        var stream: InputStream? = null
        try {
            stream =context.getContentResolver().openInputStream(uri)
            val fileName: String = System.currentTimeMillis().toString()
            val filePath: String = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + fileName
            val copyStream =
                UriToFilePath.copyStream(
                    stream,
                    filePath
                )

            if (copyStream) {
                return filePath
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return ""
        }
        return ""
    }


    /**
     * 将图片转换成Base64编码的字符串
     */
    fun imageToBase64(path: String?): String? {
        if (TextUtils.isEmpty(path)) {
            return null
        }
        var `is`: InputStream? = null
        var data: ByteArray? = null
        var result: String? = null
        try {
            `is` = FileInputStream(path)
            //创建一个字符流大小的数组。
            data = ByteArray(`is`!!.available())
            //写入数组
            `is`.read(data)
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.NO_CLOSE)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (null != `is`) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }
}