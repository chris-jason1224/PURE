package com.cj.annotation.bus.model

/**
 * Author:chris - jason
 * Date:2019-12-25.
 * Package:com.cj.annotations.bus.model
 */


/**
 * fieldName //@EventRegister注解的字段名
 * className = "java.lang.Object";//@EventRegister注解的type()值，默认为Object类型
 */
data class EventRegisterEntity(val fieldName:String="",val className:String="",val asList:Boolean=false)