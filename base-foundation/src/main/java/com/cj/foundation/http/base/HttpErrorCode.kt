package com.cj.foundation.http.base

/**
 * Author:chris - jason
 * Date:2019-12-16.
 * Package:com.cj.base_common.http.base
 * 网络请求错误码
 */
class HttpErrorCode(){

    companion object {
        //http请求联通，返回正确的业务数据
        val CODE_INT_REQUEST_OK = 9001

        //http请求联通，返回错误的业务数据或抛出了异常
        val CODE_INT_REQUEST_ERR = 9002

        //请求结果未定义
        val CODE_INT_REQUEST_UNDIFINE = 9009
    }

}
