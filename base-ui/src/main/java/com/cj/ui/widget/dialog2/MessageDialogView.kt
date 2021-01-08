package com.cj.ui.widget.dialog2

import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.view.View
import com.cj.ui.R
import com.cj.ui.widget.dialog2.base.BaseDialogView
import com.cj.ui.widget.dialog2.base.SingleBtnCallback
import org.jetbrains.annotations.NotNull
import kotlinx.android.synthetic.main.base_ui_message_dialog_view_layout.*


/**
 * Author:chris - jason
 * Date:2020-02-04.
 * Package:com.cj.base_ui.view.dialog
 * 带有单行文案 + 一个按钮，用于展示一般提示信息，无需用户确认操作
 */
class MessageDialogView @JvmOverloads constructor(
    @NotNull context: Context,
    title: String = "",
    content: String = "",
    btn: String = "",
    cancelAble: Boolean = true,
    mCallback: SingleBtnCallback? = null
) : BaseDialogView(context) {

    private var mCallback: SingleBtnCallback? = mCallback

    constructor(
        @NotNull context: Context,
        title: String = "",
        content: String = "",
        mCallback: SingleBtnCallback?
    ) : this(context, title, content, "", true, mCallback)

    init {

        if (!TextUtils.isEmpty(title)) {
            tv_title.text = title
        }

        if (!TextUtils.isEmpty(content)) {
            tv_content.text = content
        }

        if (!TextUtils.isEmpty(btn)) {
            tv_confirm_btn.text = btn
        }

        setCancelable(cancelAble)
        setCanceledOnTouchOutside(cancelAble)

    }

    override fun setDialogLayout(): Int {
        return R.layout.base_ui_message_dialog_view_layout
    }

    override fun bindView(view: View) {
        tv_confirm_btn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_confirm_btn -> {
                mCallback?.onPositive(v)
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        mCallback?.onDismiss()
    }


}
