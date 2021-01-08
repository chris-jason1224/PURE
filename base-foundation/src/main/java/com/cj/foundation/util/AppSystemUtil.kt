package com.cj.foundation.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Process
import android.text.TextUtils
import com.cj.foundation.base.BaseApp
import com.cj.foundation.log.LOG

/**
 * Author:chris - jason
 * Date:2019-12-13.
 * Package:com.cj.base_common.util
 * app工具类
 */
object AppSystemUtil {

    /**
     * 获取App版本名
     */
    fun getAppVersionName(): String {
        var context: Context = BaseApp.getInstance()
        var manager: PackageManager = context.packageManager
        var pkgInfo: PackageInfo = manager.getPackageInfo(context.packageName, 0)
        return pkgInfo.versionName
    }

    //获取App版本号
    fun getAppVersionCode(): Int {
        var context: Context = BaseApp.getInstance()
        var manager: PackageManager = context.packageManager
        var pkgInfo: PackageInfo = manager.getPackageInfo(context.packageName, 0)
        return pkgInfo.versionCode
    }

    //获取App名
    fun getAppName(): String {
        var context: Context = BaseApp.getInstance()
        var manager: PackageManager = context.packageManager
        var applicationInfo = manager.getApplicationInfo(context.packageName, 0)
        return (manager.getApplicationLabel(applicationInfo)).toString()
    }


    //判断当前进程是否是主进程
     fun isCurrentMainProcess(context: Context): Boolean {

        var manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (manager != null) {
            //获取运行的进程列表
            var runningAppProcessInfoList: MutableList<ActivityManager.RunningAppProcessInfo> =
                manager.getRunningAppProcesses();

            if (runningAppProcessInfoList != null && runningAppProcessInfoList.size > 0) {

                for (info in runningAppProcessInfoList) {
                    //当前进程 id = 运行进程 id，默认主进程名等于包名
                    if (info.pid == android.os.Process.myPid()) {
                        if (TextUtils.equals(info.processName, context.packageName)) {
                            return true
                        }
                    }
                }

            }
        }

        return false
    }


    /**
     * 自动重启app进程
     * @param delay 延时
     */
    fun restartApp(context:Context,delay:Long) {

        try {
            val intent: Intent? =  context.packageManager.getLaunchIntentForPackage(context.packageName)
            intent?.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            LOG.getInstance().e("app即将自动重启")

            Handler().postDelayed({
                //重启主进程
                context.startActivity(intent)
                //结束当前进程
                Process.killProcess(Process.myPid())
            },delay)

        } catch (e: Exception) {
            LOG.getInstance().e("crash之后重启app失败")
        }
    }

    /**
     * 判断程序是否为debug模式
     */
    fun debugAble(): Boolean {
        val info = BaseApp.getInstance().applicationInfo
        return info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    /**
     * 获取手机设备信息
     */
    fun getPhoneInformation(): String {

        var context = BaseApp.getInstance()
        var pm = context.packageManager

        var versionName =
            pm.getPackageInfo(context.packageName, PackageManager.GET_CONFIGURATIONS).versionName
        var versionCode =
            pm.getPackageArchiveInfo(
                context.packageName,
                PackageManager.GET_CONFIGURATIONS
            )!!.versionCode

        var sb = StringBuffer()

        //获取app的版本号
        sb.append("App version name:")
            .append(versionName)
            .append(", version code:")
            .append(versionCode)
            .append("\n")

        //Android版本号
        sb.append("Android OS Version: ")
        sb.append(Build.VERSION.RELEASE)
        sb.append("_")
        sb.append(Build.VERSION.SDK_INT).append("\n")

        //手机制造商
        sb.append("Vendor: ")
        sb.append(Build.MANUFACTURER).append("\n")

        //手机型号
        sb.append("Model: ")
        sb.append(Build.MODEL).append("\n")

        //CPU架构
        sb.append("CPU ABI:")
        sb.append(Build.CPU_ABI)

        sb.append("\n")

        return sb.toString()
    }

}