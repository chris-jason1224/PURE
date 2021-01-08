package com.cj.foundation.base

import androidx.lifecycle.*

/**
 * Author:chris - jason
 * Date:2019-12-10.
 * Package:com.cj.base_common.base
 * p层基础约束，实现生命周期被观察者接口
 */
 interface IBasePresenter: LifecycleObserver {
   fun attachView(v: Any)
}