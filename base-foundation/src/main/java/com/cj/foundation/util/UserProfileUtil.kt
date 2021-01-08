package com.cj.foundation.util

import android.text.TextUtils


//用户数据操作类
object UserProfileUtil {


    //保存token
    fun saveToken(token:String){
        SPUtil.saveString("user_token",token)
    }
    //获取token
    fun getToken():String{
        return SPUtil.getString("user_token","")!!
    }

    //是否登录
    fun isLogin():Boolean{
        return !TextUtils.isEmpty(getToken())
    }


}