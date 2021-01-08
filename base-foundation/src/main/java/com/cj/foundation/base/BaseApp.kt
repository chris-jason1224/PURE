package com.cj.foundation.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.hjq.toast.ToastUtils
import com.cj.foundation.bus.ModuleBus
import com.cj.foundation.log.crash.JVMCrashHandler
import com.cj.foundation.log.LOG
import com.cj.foundation.log.config.LogConfig
import com.cj.foundation.module.IModuleDelegate
import com.cj.foundation.module.ModuleManager
import com.cj.foundation.util.AppSystemUtil
import com.cj.foundation.util.SPUtil
import com.cj.ui.widget.dialog2.ConfirmDialogView
import com.cj.ui.widget.dialog2.base.DoubleBtnCallback
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import java.lang.ref.WeakReference

/**
 * Author:chris - jason
 * Date:2019-12-10.
 * Package:com.cj.base_common.base
 */
open class BaseApp : Application() {

    private var mCurrentCount = 0//onStart执行后，activity数量
    private var isCurrent = false
    private var delegateList: MutableList<IModuleDelegate>? = mutableListOf()
    private var mCurrentActivity: WeakReference<Activity>? = null
    var mActivityList: MutableList<Activity> = mutableListOf()
    private var mResumeActivity: MutableList<Activity> = mutableListOf()

    companion object {
        @Volatile
        private var instance: BaseApp? = null

        //获取Application实例
        fun getInstance() = instance!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        /**
         * 多进程app会重复启动Application，只在主进程中执行一次即可
         */
        if (!AppSystemUtil.isCurrentMainProcess(this)) {
            return
        }

        //初始化日志模块
        LOG.initLogConfig(LogConfig(false,30),this)


        /**
         * 拦截主线程异常
         * 通过Handler往主线程的queue中添加一个Runnable，当主线程执行到该Runnable时，会进入我们的while死循环，如果while内部是空的就会导致代码卡在这里，最终导致ANR。
         * 我们在while死循环中又调用了Looper.loop()，这就导致主线程又开始不断的读取queue中的Message并执行，也就是主线程并不会被阻塞。同时又可以保证以后主线程的所有异常都会从我们手动调用的Looper.loop()处抛出，一旦抛出就会被try-catch捕获，这样主线程就不会crash了。
         * 通过while(true)让主线程抛出异常后迫使主线程重新进入我们try-catch中的消息循环。 如果没有这个while的话那么主线程在第二次抛出异常时我们就又捕获不到了，这样APP就又crash了。
         */
        Handler(Looper.getMainLooper()).post(Runnable {
            while (true) {
                try {
                    //主线程的异常会从这里抛出
                    Looper.loop()
                } catch (e: Exception) {
                    //主线程发生崩溃
                    e.printStackTrace()
                    LOG.getInstance().e("JVM-CRASH-LOG:", e.message!!)
                    val act: Activity = getTopActivity()
                    if (act == null || act.isFinishing) {
                        return@Runnable
                    }
                    ConfirmDialogView(act, "温馨提示",
                        "程序在运行期间发生错误，是否上传日志？",
                        "取消", "确定",
                        false,
                        object : DoubleBtnCallback{
                            override fun onDismiss() {}
                            override fun onNegative(view: View) {
                                AppSystemUtil.restartApp(applicationContext,1000)
                            }

                            override fun onPositive(view: View) {
                                LOG.getInstance().uploadLogSilently(act)
                            }
                        }).show()
                }
            }
        })

        /**拦截子线程异常 */
        Thread.setDefaultUncaughtExceptionHandler(JVMCrashHandler)

        //RxJava统一拦截
        RxJavaPlugins.setErrorHandler(object : Consumer<Throwable>{
            override fun accept(t: Throwable?) {
                LOG.getInstance().e("RxJava Global error is ${t?.message}")
            }
        })

        ToastUtils.init(this)
        SPUtil.init(this)

        //注册Application生命周期观测
        this.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
            }

            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                mActivityList.add(0, p0)
            }

            override fun onActivityResumed(p0: Activity) {
                if (!mResumeActivity.contains(p0)) {
                    mResumeActivity.add(p0)
                }
                mCurrentActivity = WeakReference(p0)
            }

            override fun onActivityStarted(p0: Activity) {
                if (mCurrentCount == 0 && !isCurrent) {
                    isCurrent = true
                    for (delegate in delegateList!!) {
                        delegate.enterForeground()
                    }
                    Log.d("BaseApplication", "The App go to foreground")
                }
                mCurrentCount++
            }

            override fun onActivityPaused(p0: Activity) {

            }

            override fun onActivityStopped(p0: Activity) {
                if (mResumeActivity.contains(p0)) {
                    mResumeActivity.remove(p0)
                    mCurrentCount--
                }

                if (mCurrentCount == 0 && isCurrent) {
                    isCurrent = false
                    for (delegate in delegateList!!) {
                        delegate.enterBackground()
                    }
                }
            }

            override fun onActivityDestroyed(p0: Activity) {
                if (mActivityList.contains(p0)) {
                    mActivityList.remove(p0)
                }
            }
        })

        //加载各个组件
        ModuleManager.loadModules()
        delegateList = ModuleManager.delegateList

        //回调onCreate()
        if (delegateList != null) {
            for (delegate in delegateList!!) {
                delegate.onCreate(this)
            }
        }

        //初始化ARouter
        if (debugAble()) {
            ARouter.openLog()
            ARouter.openDebug()
        }

        ARouter.init(this)

        ModuleBus.getInstance().init(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        //开启multiDex多分包
        MultiDex.install(this)
    }

    private fun debugAble(): Boolean {
        val info = applicationInfo
        return info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    fun getTopActivity():Activity{
        return mCurrentActivity?.get()!!
    }

}