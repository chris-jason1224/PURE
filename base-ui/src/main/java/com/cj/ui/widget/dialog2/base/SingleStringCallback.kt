package com.cj.ui.widget.dialog2.base


interface SingleStringCallback: BaseDialogViewCallback {
    fun onPositive(inputStr:String):Boolean
}