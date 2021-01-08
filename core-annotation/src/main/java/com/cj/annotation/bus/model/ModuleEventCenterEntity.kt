package com.cj.annotation.bus.model

/**
 * Author:chris - jason
 * Date:2019-12-25.
 * Package:com.cj.annotations.bus.model
 * 每个module中注解添加的类名
 */
class ModuleEventCenterEntity(moduleName:String,delegateName:String,pkgName:String){
    var moduleName:String = moduleName
    var delegateName:String = delegateName
    var pkgName:String = pkgName
}