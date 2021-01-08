package com.cj.foundation.log

import android.app.Activity
import android.content.Context
import android.os.Environment
import android.os.Process
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.cj.foundation.log.config.LogConfig
import com.cj.foundation.util.AppSystemUtil.debugAble
import com.cj.foundation.util.AppSystemUtil.getPhoneInformation
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


/**
 * Author:chris - jason
 * Date:2020-02-02.
 * Package:com.cj.base_common.log
 * 日志打印工具类
 */

class LOG private constructor() {

    private lateinit var mConfig: LogConfig
    private lateinit var mContext:Context

    private lateinit var dirNormal:String
    private lateinit var dirCrash:String

    companion object {
        @Volatile
        private var INSTANCE: LOG? = null

        fun initLogConfig(config: LogConfig,context: Context): LOG {
            if (INSTANCE == null) {
                synchronized(LOG::class) {
                    if (INSTANCE == null) {
                        INSTANCE = LOG()
                        INSTANCE!!.mContext = context
                        INSTANCE!!.mConfig = config

                        INSTANCE!!.dirNormal = "${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.absolutePath}${File.separator}log${File.separator}normal${File.separator}"
                        INSTANCE!!.dirCrash = "${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.absolutePath}${File.separator}log${File.separator}crash${File.separator}"
                    }
                }
            }
            return INSTANCE!!
        }

        fun getInstance(): LOG {
            return INSTANCE!!
        }
    }

    init {
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    //日志分级
    private var Log_Level_Normal = "log_level_Normal"//普通日志
    private var Log_Level_Error = "Log_Level_Error"//错误信息日志

    //时间格式
    private val dateFormat: SimpleDateFormat by lazy {
        SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.CHINA
        )
    }

    private val EXECUTOR by lazy { Executors.newSingleThreadExecutor() }

    fun e(message: String) {

        if (debugAble()) {
            Logger.e(message)
        }

        append2File(message, Log_Level_Error)
    }

    fun e(tag:String,message: String) {

        if (debugAble()) {
            Logger.e(tag,message)
        }

        append2File(message, Log_Level_Error)
    }

    fun d(message: String) {

        if (debugAble()) {
            Logger.d(message)
        }

        append2File(message, Log_Level_Normal)
    }

    fun d(tag:String,message: String) {

        if (debugAble()) {
            Logger.d(tag,message)
        }

        append2File(message, Log_Level_Normal)
    }

    fun w(message: String) {

        if (debugAble()) {
            Logger.w(message)
        }

        append2File(message, Log_Level_Normal)
    }

    fun w(tag:String,message: String) {

        if (debugAble()) {
            Logger.w(tag,message)
        }

        append2File(message, Log_Level_Normal)
    }

    fun json(json: String) {

        if (debugAble()) {
            Logger.json(json)
        }

        append2File(json, Log_Level_Normal)
    }

    //将日志追加到本地文件中
    private fun append2File(content: String, level: String) {
        try {

            var dirFile = File(dirNormal)
            if (!dirFile.exists()) {
                dirFile.mkdirs()
            }

            //日志文件
            var filePath = "${dirNormal}logcat.txt"

            val file = File(filePath)
            if(!file.exists()){
                file.createNewFile()
            }

            val sb = StringBuilder()
            val pid = Process.myPid()
            val time = dateFormat.format(Date(System.currentTimeMillis()))

            val threadId = Thread.currentThread().id
            sb.append("tag:func-log" )
                .append("$time")
                .append(">=pid:=> $pid  ")
                .append(">=thread:=>$threadId ")
                .append(content)
                .append("              ")

            val log = sb.toString()

            EXECUTOR.execute {
                var bw: BufferedWriter? = null
                try {
                    bw = BufferedWriter(FileWriter(filePath, true))
                    bw.write(log)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Logger.e("写入日志失败："+e.message)
                } finally {
                    try {
                        bw?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

        } catch (e: Exception) {
            Logger.e("" + e.message)
        }

    }

    //将崩溃日志保存为文件
    fun saveCrashLog(ex: Throwable) {

        try {
            Logger.e("" + ex.message)

            var dirFile = File(dirCrash)
            if (!dirFile.exists()) {
                dirFile.mkdirs()
            }

            var time = dateFormat.format(Date(System.currentTimeMillis()))
            var file = File("${dirCrash}crash_${time}.txt")

            if (file.exists()) {
                file.delete()
            }

            file.createNewFile()

            var pw = PrintWriter(BufferedWriter(FileWriter(file)))
            pw.println(time)
            pw.println()
            pw.println(getPhoneInformation())
            pw.println()
            ex.printStackTrace(pw)
            pw.close()
            Logger.e("崩溃日志保存成功:--> ${file.absolutePath}")
        } catch (e: Exception) {
            Logger.e("" + e.message)
        }

    }

    //日志上传 1、压缩日志 2、允许用户输入备注 3、上传
    /**
     * 静默上传日志
     */
    fun uploadLogSilently(activity: Activity){
        //todo
    }

    /**
     * 公开上传
     */
    fun uploadLogPublicly(activity:Activity){
        //todo
    }

}