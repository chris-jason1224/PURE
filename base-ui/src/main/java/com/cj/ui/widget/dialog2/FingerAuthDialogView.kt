package com.cj.ui.widget.dialog2

import android.content.Context
import android.content.DialogInterface
import android.view.View
import com.cj.ui.R
import com.cj.ui.widget.dialog2.base.AuthDialogViewCallback
import com.cj.ui.widget.dialog2.base.BaseDialogView
import kotlinx.android.synthetic.main.base_ui_finger_auth_dialog_view_layout.*

/**
 * 指纹验证弹框
 */
class FingerAuthDialogView constructor(
    context: Context,
    title:String,
    callback: AuthDialogViewCallback
) : BaseDialogView(context) {

    private var callback: AuthDialogViewCallback = callback

    init {
        //setCancelable(false)
        //setCanceledOnTouchOutside(false)
        tv_title.text = title
    }
    override fun setDialogLayout(): Int {
        return R.layout.base_ui_finger_auth_dialog_view_layout
    }

    override fun bindView(view: View) {
        tv_negative_btn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when(v?.id){
            R.id.tv_negative_btn ->{
                dismiss()
            }
        }

    }

    override fun onDismiss(dialog: DialogInterface?) {
        callback.onCloseView()
    }

}