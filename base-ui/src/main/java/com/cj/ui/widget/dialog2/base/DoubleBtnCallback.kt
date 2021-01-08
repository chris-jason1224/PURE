package com.cj.ui.widget.dialog2.base
import android.view.View

/**
 * Author:chris - jason
 * Date:2020-02-05.
 * Package:com.cj.base_ui.view.dialog.base
 *两个按钮的弹框回调
 */
interface DoubleBtnCallback: BaseDialogViewCallback {
    fun onPositive(view: View)
    fun onNegative(view:View)
}