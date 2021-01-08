package com.cj.ui.widget.dialog2.base


import android.view.View
import com.cj.ui.widget.dialog2.base.BaseDialogViewCallback

interface DoubleBtn_RadioButton_Callback: BaseDialogViewCallback {
    fun onPositive(inputContent:String,validateDate: Int,extractCodeValidTimes:String)
    fun onNegative(view: View)
}