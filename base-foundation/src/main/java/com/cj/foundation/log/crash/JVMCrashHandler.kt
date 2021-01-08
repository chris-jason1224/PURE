package com.cj.foundation.log.crash

import android.os.Looper
import android.view.View
import com.cj.foundation.base.BaseApp
import com.cj.foundation.log.LOG
import com.cj.foundation.util.AppSystemUtil
import com.cj.ui.widget.dialog2.ConfirmDialogView
import com.cj.ui.widget.dialog2.base.DoubleBtnCallback


object JVMCrashHandler : Thread.UncaughtExceptionHandler {

    private val mDefaultCrashHandler: Thread.UncaughtExceptionHandler by lazy { Thread.getDefaultUncaughtExceptionHandler() }

    var crash = false

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        crash = true

        if (!handleException(e)) {
            //如果自己没处理交给系统处理
            mDefaultCrashHandler.uncaughtException(t, e)
            return
        }

        object : Thread() {
            override fun run() {
                Looper.prepare()

                var act = BaseApp.getInstance().getTopActivity()
                if(act==null || act.isFinishing){
                    return
                }

                ConfirmDialogView(act, "温馨提示",
                    "程序在运行期间发生错误，是否上传日志？",
                    "取消", "确定", false,
                    object : DoubleBtnCallback {
                        override fun onDismiss() {}
                        override fun onNegative(view: View) {
                            AppSystemUtil.restartApp(BaseApp.getInstance(), 1000)
                        }

                        override fun onPositive(view: View) {
                            LOG.getInstance().uploadLogSilently(act)
                        }
                    }).show()

                Looper.loop()
            }
        }.start()

    }

    //处理了该异常返回true, 否则false
    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }
        //保存日志文件
        LOG.getInstance().e("JVM-CRASH-LOG:", ex.message.toString())
        return true
    }

}