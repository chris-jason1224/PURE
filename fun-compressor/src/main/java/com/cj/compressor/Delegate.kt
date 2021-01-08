package com.cj.compressor

import android.content.Context
import com.cj.annotation.module.ModuleRegister
import com.cj.compressor.core.EasyCompressor
import com.cj.foundation.module.IModuleDelegate

@ModuleRegister
class Delegate: IModuleDelegate {

    override fun onCreate(context: Context) {
        EasyCompressor.init(context)
    }

    override fun enterForeground() {
    }

    override fun enterBackground() {
    }
}