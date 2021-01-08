package com.cj.ui.widget.dialog2.base

/**
 * AuthDialogView事件回掉接口
 */
interface AuthDialogViewCallback {
    //窗口关闭回掉
    fun onCloseView()
    //切换成密码验证方式
    fun switch2Pwd()
    //切换成指纹验证方式
    fun switch2Finger()
    //密码输入完成
    fun onPwdDone(inputStr:String)
}