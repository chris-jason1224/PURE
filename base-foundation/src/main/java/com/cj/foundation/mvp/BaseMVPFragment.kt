package com.cj.foundation.mvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cj.foundation.base.BaseFragment
import com.cj.foundation.base.IBasePresenter


/**
 * Author:chris - jason
 * Date:2020/4/15.
 * Package:com.cj.base_common.mvp
 */
abstract class BaseMVPFragment<P : IBasePresenter> : BaseFragment() {

    var mPresenter: P? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //创建presenter
        mPresenter = createPresenter()

        //管理P层生命周期
        lifecycle.addObserver(mPresenter!!)

        //绑定p、v
        mPresenter!!.attachView(this)

        //完成view和p层绑定之后再执行BaseFragment.onCreateView()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    abstract fun createPresenter(): P

}