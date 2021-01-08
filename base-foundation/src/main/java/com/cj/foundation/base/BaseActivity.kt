package com.cj.foundation.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.cj.foundation.R
import com.cj.foundation.network.NetworkCenter
import com.cj.ui.qmui.helper.QMUIDisplayHelper
import com.cj.ui.qmui.helper.QMUIStatusBarHelper
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


/**
 * Author:chris - jason
 * Date:2019-12-10.
 * Package:com.cj.base_common.base
 * Activity最底层基类
 */
abstract class BaseActivity() : AppCompatActivity(), View.OnClickListener,
    NetworkCenter.OnNetworkChangedListener,
    IBaseView, CoroutineScope {

    //activity是否处于前台
    var isActivityReady: Boolean = false;
    var mContentView: View? = null

    //协程
    private val job: Job by lazy { Job() }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val coroutineScope: CoroutineScope by lazy { CoroutineScope(coroutineContext) }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //为activity指定全屏的主题，隐藏掉actionbar
        setTheme(R.style.base_common_Theme)
        //加载布局
        mContentView = LayoutInflater.from(this).inflate(resourceLayout(), null);
        setContentView(mContentView!!)
        ARouter.getInstance().inject(this)
        //注册网络监听
        NetworkCenter.register(this, this)

        initView()

        initData()
    }

    abstract fun resourceLayout(): Int

    abstract fun initView()

    abstract fun initData()

    override fun onStart() {
        super.onStart()
        isActivityReady = true
    }

    override fun onStop() {
        super.onStop()
        isActivityReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        //取消注册网络变化
        NetworkCenter.unRegister(this)
        //取消协程作用范围
        job.cancel()
    }

    //activity基类提供一个默认的网络变化处理，子类自行覆写
    override fun onNetworkChanged(isConnected: Boolean) {
        //当前activity处于前台时，提示用户
        if (isActivityReady) {
            //todo 弹出提示网络变化

        }
    }

    override fun getContext(): Context {
        return this
    }


}