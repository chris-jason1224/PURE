package com.cj.editor

import android.content.Context
import com.cj.annotation.module.ModuleRegister
import com.cj.foundation.bus.ModuleBus
import com.cj.foundation.log.LOG
import com.cj.foundation.module.IModuleDelegate


@ModuleRegister
class Delegate:IModuleDelegate {
    override fun onCreate(context: Context) {
        LOG.getInstance().e("fun-editor onCreate......")
    }

    override fun enterForeground() {

    }

    override fun enterBackground() {
    }
}