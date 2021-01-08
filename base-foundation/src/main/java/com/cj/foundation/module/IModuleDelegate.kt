package com.cj.foundation.module

import android.content.Context

/**
 * Author:chris - jason
 * Date:2020-01-17.
 * Package:com.cj.base_common.module
 */
interface IModuleDelegate {

    //创建
    fun onCreate(context: Context)

    //进入前台
    fun enterForeground()

    //进入后台
    fun enterBackground()

}