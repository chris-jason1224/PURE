package com.cj.foundation.http.rx

import com.hjq.toast.ToastUtils
import com.cj.foundation.http.base.BaseHttpResultEntity
import com.cj.foundation.http.base.HttpCallback
import com.cj.foundation.http.base.HttpErrorCode
import com.cj.foundation.util.AppSystemUtil
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import retrofit2.HttpException

import java.net.SocketTimeoutException

/**
 * Author:chris - jason
 * Date:2019-12-16.
 * Package:com.cj.base_common.http.rx
 * RX封装的观察者,统一拦截请求结果
 * T是业务数据data的类型，一般是 JSONObject、JSONArray、Object
 * M是实际数据实体的泛型
 */
class HttpResultObserver<T> constructor(
    var mDisposable: CompositeDisposable,
    var mCallback: HttpCallback<T>
) : DisposableObserver<BaseHttpResultEntity<T>>(), Observer<BaseHttpResultEntity<T>> {

    init {
        mDisposable!!.add(this)
    }

    override fun onNext(t: BaseHttpResultEntity<T>) {

        //可以在debug模式下打印日志，okhttpClient中也添加了日志拦截器
        if (AppSystemUtil.debugAble()) {
            //todo 打印日志
        }

        when (t.errorCode) {
            HttpErrorCode.CODE_INT_REQUEST_OK -> mCallback.onSuccess(t.data)
            HttpErrorCode.CODE_INT_REQUEST_ERR -> mCallback.onFailed(t.errorCode, t.message)
            else -> mCallback.onFailed(HttpErrorCode.CODE_INT_REQUEST_UNDIFINE, t.message)
        }

    }

    override fun onError(e: Throwable) {

        if (AppSystemUtil.debugAble()) {
            e.printStackTrace()
        }

        var error = e.toString()

        when (e) {
            //连接超时
            is SocketTimeoutException -> showAlert("网络连接超时")
            //常见服务端问题 404、500等
            is HttpException -> let {
                showAlert("${e.code()}" + e.response().toString())
            }
            //其他异常
            else -> mCallback.onFailed(HttpErrorCode.CODE_INT_REQUEST_UNDIFINE, error)
        }

    }

    override fun onComplete() {

    }

    private fun showAlert(message: String) {
        ToastUtils.show(message)
    }


}