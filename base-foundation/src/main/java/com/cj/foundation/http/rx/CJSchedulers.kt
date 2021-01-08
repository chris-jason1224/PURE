package com.cj.foundation.http.rx

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers


/**
 * Author:chris - jason
 * Date:2019-12-13.
 * Package:com.cj.base_common.http.rx
 * 网络请求时子线程和主线程切换
 */
class CJSchedulers {

    companion object {

//        fun <T> compose1(): ObservableTransformer<T, T> {
//
//            var otf: ObservableTransformer<T, T> = ObservableTransformer() { upstream ->
//
//                var os: ObservableSource<T> = upstream.subscribeOn(Schedulers.io())
//                    .doOnSubscribe(object : Consumer<Disposable> {
//                        override fun accept(t: Disposable?) {
//
//                        }
//                    }).observeOn(AndroidSchedulers.mainThread())
//
//                return@ObservableTransformer os
//            }
//
//            return otf
//        }

        fun <T> compose(): ObservableTransformer<T, T> {
            return ObservableTransformer { upstream ->
                return@ObservableTransformer upstream.subscribeOn(Schedulers.io())
                    .doOnSubscribe(object : Consumer<Disposable> {
                        override fun accept(t: Disposable?) {
                        }
                    }).observeOn(AndroidSchedulers.mainThread())
            }
        }

    }
}