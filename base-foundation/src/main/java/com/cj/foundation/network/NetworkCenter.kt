package com.cj.foundation.network

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.cj.foundation.base.BaseApp

/**
 * Author:chris - jason
 * Date:2019-12-17.
 * Package:com.cj.base_common.network
 * android N开始移除 CONNECTIVITY_ACTION intent，改用NetworkCallback进行网络状态变化监听
 * 网络中心
 */
object NetworkCenter {

    private var mManager:ConnectivityManager? = null
    private var mListeners :HashMap<String,OnNetworkChangedListener>?=null
    private var mNetworkCallback:ConnectivityManager.NetworkCallback?=null

    init {
        mManager = BaseApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        mListeners = HashMap()

        var netRequest = NetworkRequest.Builder().
            //addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).
            //addTransportType(NetworkCapabilities.TRANSPORT_WIFI).
            build()

        mNetworkCallback = object :ConnectivityManager.NetworkCallback(){
            override fun onUnavailable() {
                super.onUnavailable()
                dispatch(false)
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                dispatch(true)
            }
        }

        mManager!!.registerNetworkCallback(netRequest, mNetworkCallback!!)
    }

    /**
     * 分别提供在activity和fragment中进行注册和反注册的方法，以便可以单独处理
     */
    fun register(act: Activity, listener:OnNetworkChangedListener) {

        if(act==null){
            return
        }

        var actName = act.javaClass.toString()
        if(TextUtils.isEmpty(actName)){
            return
        }
        mListeners!!.put(actName,listener)
    }

    fun register(frg: Fragment, listener:OnNetworkChangedListener) {

        if(frg==null){
            return
        }

        var actName = frg.javaClass.toString()
        if(TextUtils.isEmpty(actName)){
            return
        }
        mListeners!!.put(actName,listener)
    }

    //移除回调
    fun unRegister(act: Activity) {
        if(act==null){
            return
        }

        var actName = act.javaClass.toString()
        if(TextUtils.isEmpty(actName)){
            return
        }
        mListeners!!.remove(actName)
    }

    fun unRegister(frg: Fragment) {
        if(frg==null){
            return
        }

        var actName = frg.javaClass.toString()
        if(TextUtils.isEmpty(actName)){
            return
        }
        mListeners!!.remove(actName)
    }

    //取消注册监听
    fun unRegisterAll(){
        mListeners!!.clear()
        mManager!!.unregisterNetworkCallback(mNetworkCallback!!)
    }


    //网络变化回调接口
    interface OnNetworkChangedListener {
        fun onNetworkChanged(isConnected: Boolean)
    }

    private fun dispatch(isConnected: Boolean){
        for (map in mListeners!!){
            map.value.onNetworkChanged(isConnected)
        }
    }


}