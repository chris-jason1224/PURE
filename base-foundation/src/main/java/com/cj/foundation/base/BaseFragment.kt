package com.cj.foundation.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData

/**
 * Author:chris - jason
 * Date:2019-12-10.
 * Package:com.cj.base_common.base
 * Fragment最底层基类
 */
abstract class BaseFragment : Fragment(), View.OnClickListener {

    //是否懒加载
    var isLazyLoad: Boolean = false
    //宿主Activity
    var mActivity: FragmentActivity? = null;
    //根View
    var mRootView: View? = null
    //是否对用户可见
    var mIsVisible: Boolean = false
    //是否加载完成，当执行完onCreateView之后即为true，标识碎片加载完成
    var mIsPrepare: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //1.加载布局 XML 文件
        mRootView = inflater.inflate(setLayoutResource(), container, false)
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState);
        mIsPrepare = true;
        isLazyLoad = setLazyLod();

        //非懒加载模式，直接初始化数据
        if (!isLazyLoad) {
            initData();
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mIsVisible = isVisible
        if (mIsVisible) {
            onVisibleToUser()
        }
    }

    /**
     * 用户可见时执行的操作
     */
    private fun onVisibleToUser() {
        //只有碎片加载完成和可见的时候，执行懒加载数据
        if (mIsPrepare && mIsVisible) {
            //只需要懒加载一次
            mIsPrepare = false;
            initData();
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //Activity被回收后，Fragment不会被回收
        mActivity = activity
    }

    override fun onClick(v: View?) {

    }

    abstract fun setLayoutResource(): Int

    abstract fun initData()

    abstract fun setLazyLod(): Boolean

}