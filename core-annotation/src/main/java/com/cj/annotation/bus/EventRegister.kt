package com.cj.annotation.bus

import kotlin.reflect.KClass

/**
 * Author:chris - jason
 * Date:2020-01-02.
 * Package:com.cj.annotations.bus
 */

/**
 * @param type 泛型类型
 * @param list 表明传递的类型是否是集合
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class EventRegister(val classType:KClass<*> = Any::class,val asList:Boolean = false)
