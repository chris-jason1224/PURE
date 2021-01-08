package com.cj.foundation.http.base

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author:chris - jason
 * Date:2019-12-13.
 * Package:com.cj.base_common.http.base
 */
class BaseHttpResultEntity<M> : Serializable {

    constructor(errorCode: Int, data: M, message: String) {
        this.errorCode = errorCode
        this.data = data
        this.message = message
    }

    //错误码
    @SerializedName("code")
    var errorCode: Int

    //实际数据包
    @SerializedName("data")
    var data: M

    @SerializedName("msg")
    var message: String


}