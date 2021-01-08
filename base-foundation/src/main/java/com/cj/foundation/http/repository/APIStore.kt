package com.cj.foundation.http.repository

import com.cj.foundation.http.base.BaseHttpResultEntity
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Author:chris - jason
 * Date:2019-12-10.
 * Package:com.cj.base_common.http.repository
 */
interface APIStore {

    //测试我自己写的接口
    @FormUrlEncoded
    @POST("/dev/api/json")
    fun testJSON(@Field("uid") uid: String): Observable<BaseHttpResultEntity<List<Any>>>

}