package com.cj.foundation.http.base

/**
 * Author:chris - jason
 * Date:2019-12-16.
 * Package:com.cj.base_common.http.base
 */
interface HttpCallback<M> {

     fun onSuccess(m: M)

     fun onFailed(errCode:Int,msg: String)

}