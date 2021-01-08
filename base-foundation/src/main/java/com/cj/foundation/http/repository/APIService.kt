package com.cj.foundation.http.repository

import android.content.Context
import android.os.Build
import android.webkit.WebSettings
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.cj.foundation.base.BaseApp
import com.cj.foundation.util.AppSystemUtil
import com.cj.foundation.util.UserProfileUtil
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Author:chris - jason
 * Date:2019-12-13.
 * Package:com.cj.base_common.http.repository
 */
object APIService {

    //网络请求基地址
    private val BASE_URL: String? = "http://172.16.10.35:8080"
    //最大缓存量 10M
    private val mMaxCacheSize = 1024 * 1024 * 10L
    private var mRetrofit: Retrofit? = null

    init {
        createRetrofit()
    }

    //构建缓存
    private fun buildCache(context: Context?): Cache {
        var file = File(context!!.cacheDir.absolutePath)
        var cache = Cache(file, mMaxCacheSize)
        return cache
    }

    //构建HttpClient
    private fun createHttpClient(): OkHttpClient {

        var builder: OkHttpClient.Builder = OkHttpClient.Builder()

        //添加日志拦截器，打印请求、响应信息
        if (AppSystemUtil.debugAble()) {
            var loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
        }
        builder.cache(buildCache(BaseApp.getInstance())).readTimeout(5, TimeUnit.SECONDS).//读取超时
            writeTimeout(15, TimeUnit.SECONDS).//写入超时
            connectTimeout(15, TimeUnit.SECONDS).//连接超时时间
            retryOnConnectionFailure(true)//自动断线重连

        builder.addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                //原始请求
                var request: Request = chain.request()
                //拦截修改后的请求
                var newBuilder: Request.Builder = request.newBuilder()
                //添加用户token
                newBuilder.addHeader("UserToken", UserProfileUtil.getToken())
                //添加User-Agent
                var agent: String =
                    if (Build.VERSION.SDK_INT > 17) WebSettings.getDefaultUserAgent(BaseApp.getInstance()) else System.getProperty(
                        "http.agent"
                    )
                newBuilder.addHeader("User-Agent", agent)
                //添加app版本信息
                newBuilder.addHeader("AppVersionName", AppSystemUtil.getAppVersionName())
                newBuilder.addHeader("AppVersionCode", AppSystemUtil.getAppVersionCode().toString())
                //添加ContentType
                newBuilder.addHeader(
                    "Content-Type",
                    "application/x-www-form-urlencoded; charset=UTF-8"
                )
                newBuilder.addHeader("Accept-Encoding", "gzip,deflate")
                newBuilder.addHeader("Connection", "keep-alive")
                newBuilder.addHeader("Accept", "*/*");

                return chain.proceed(newBuilder.build())
            }
        })


        return builder.build()
    }

    //构建Retrofit
    private fun createRetrofit() {

        var gson = GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).setPrettyPrinting()
            .setVersion(1.0).create()

        mRetrofit = Retrofit.Builder().client(createHttpClient()).
            baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }

    open fun <T> createAPI(clazz: Class<T>): T {
        return mRetrofit!!.create(clazz)
    }


}