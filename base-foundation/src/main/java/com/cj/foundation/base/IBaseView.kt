package com.cj.foundation.base

import android.content.Context
import androidx.lifecycle.LifecycleOwner

/**
 * Author:chris - jason
 * Date:2019-12-10.
 * Package:com.cj.base_common.base
 * View层基础约束，实现生命周期被观察者接口
 */
interface IBaseView : LifecycleOwner {
    /**
     * 用于返回activity或者fragment实例
     */
    fun getContext(): Context?
}