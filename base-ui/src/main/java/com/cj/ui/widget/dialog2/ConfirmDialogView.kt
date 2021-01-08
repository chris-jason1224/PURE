package com.cj.ui.widget.dialog2
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.view.View
import com.cj.ui.R
import com.cj.ui.widget.dialog2.base.BaseDialogView
import com.cj.ui.widget.dialog2.base.DoubleBtnCallback
import kotlinx.android.synthetic.main.base_ui_confirm_dialog_view_layout.*
import org.jetbrains.annotations.NotNull

/**
 * Author:chris - jason
 * Date:2020-02-11.
 * Package:com.cj.base_ui.view.dialog
 * 确认对话框 标题+内容+两个按钮
 */

class ConfirmDialogView @JvmOverloads constructor(
    @NotNull context: Context,
    title: String,
    content: String = "",
    negative: String = "",
    positive: String = "",
    cancelAble: Boolean = true,
    callback: DoubleBtnCallback? = null
) : BaseDialogView(context) {

    private var mCallback: DoubleBtnCallback? = callback

    constructor(
        @NotNull context: Context,
        title: String,
        content: String = "",
        cancelAble: Boolean = true,
        callback: DoubleBtnCallback? = null
    ) : this(context, title, content, "", "", cancelAble, callback)


    init {
        if(!TextUtils.isEmpty(title)){
            tv_title.text = title
        }

        if(!TextUtils.isEmpty(content)){
            tv_content.text = content
        }

        if(!TextUtils.isEmpty(negative)){
            tv_negative_btn.text = negative
        }


        if(!TextUtils.isEmpty(positive)){
            tv_positive_btn.text = positive
        }

        setCancelable(cancelAble)
        setCanceledOnTouchOutside(cancelAble)
    }

    override fun setDialogLayout(): Int {
        return R.layout.base_ui_confirm_dialog_view_layout
    }

    override fun bindView(root: View) {
        tv_negative_btn.setOnClickListener(this)
        tv_positive_btn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.tv_negative_btn -> mCallback?.onNegative(v)
            R.id.tv_positive_btn -> mCallback?.onPositive(v)
        }

        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        mCallback?.onDismiss()
    }

}
