package com.cj.foundation.interceptor

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.alibaba.android.arouter.launcher.ARouter
import com.cj.foundation.const.TAG_USER_NEED_LOGIN
import com.cj.foundation.util.UserProfileUtil

/**
 * Author:chris - jason
 * Date:2019-12-19.
 * Package:com.cj.base_common.interceptor
 *
 * 登录拦截器
 *
 * 所有通过路由进行页面跳转都要进入这个拦截器
 * 1.Activity：/组件代号/ACT/{USR}/activity_name
 * 2.Fragment：/组件代号/FRG/fragment_name
 * 3.组件服务：/组件代号/SEV/service_name
 * 路由包含USR则表示该页面需要登录后查看
 *
 */
@Interceptor(priority = 1, name = "login-interceptor")
open class LoginInterceptor : IInterceptor {

    private var mContext: Context? = null
    private var mCallback: InterceptorCallback? = null
    private var mPostcard: Postcard? = null

    override fun init(context: Context?) {
        this.mContext = context
    }

    override fun process(postcard: Postcard?, callback: InterceptorCallback?) {

        if (postcard!!.path.contains(TAG_USER_NEED_LOGIN)) {
            mPostcard = postcard
            mCallback = callback

            //判断是否登录，未登录跳转登录
            if (UserProfileUtil.isLogin()) {
                //跳转登录页面时应加greenchannel，避免循环进入拦截器
                ARouter.getInstance().build("/biz_login/ACT/com.cj.biz_login.LoginActivity").greenChannel().navigation()
                //阻断拦截过程，避免超时
                //callback!!.onInterrupt(null)
            } else {
                callback!!.onContinue(postcard)
            }
        }
    }


}