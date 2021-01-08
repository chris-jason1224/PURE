package com.cj.annotation.module

/**
 * Author:chris - jason
 * Date:2020-01-17.
 * Package:com.cj.annotations.module
 * 组件注册注解，编译期间自动为添加该注解的module生成配置文件，并完成加载，生命周期回调
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ModuleRegister(val enable:Boolean = true)