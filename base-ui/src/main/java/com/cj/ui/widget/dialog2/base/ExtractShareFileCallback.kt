package com.cj.ui.widget.dialog2.base

interface ExtractShareFileCallback: BaseDialogViewCallback {
    fun onCancel()
    fun onExtractResult(result:Boolean,msg:String):Boolean
}