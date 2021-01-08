package com.cj.pure

import com.cj.annotation.bus.EventRegister
import com.cj.annotation.bus.ModuleEventCenter

@ModuleEventCenter
class event {
    @EventRegister(classType = String::class)
    val ssss:String = ""
}