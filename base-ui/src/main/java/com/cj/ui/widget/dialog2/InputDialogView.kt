package com.cj.ui.widget.dialog2

import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.view.*
import com.cj.ui.R
import com.cj.ui.widget.dialog2.base.BaseDialogView
import com.cj.ui.widget.dialog2.base.SingleStringCallback
import com.cj.ui.util.AutoLiftUtil
import com.cj.ui.util.KeyboardUtil
import kotlinx.android.synthetic.main.base_ui_input_dialog_view_layout.*
import org.jetbrains.annotations.NotNull


/**
 * Author:chris - jason
 * Date:2020-02-04.
 * Package:com.cj.base_ui.view.dialog
 * 带输入框的弹出框，可回调输入内容
 *
 * @param inputType @link{android.R.attr.editTextStyle.inputType}
 */
class InputDialogView @JvmOverloads constructor(
    @NotNull context: Context,
    inputType: Int = 0x00000001,//默认输入类型为文本
    title: String = "",
    content: String = "",
    input: String = "",
    hint: String = "",
    negative: String = "",
    positive: String = "",
    cancelAble: Boolean = true,
    mCallback: SingleStringCallback? = null
) : BaseDialogView(context) {

    private var inputType: Int = inputType
    private var title: String = title
    private var content: String = content
    private val input: String = input
    private var hint: String = hint
    private var negative: String = negative
    private var positive: String = positive
    private var mCallback: SingleStringCallback? = mCallback

    constructor(
        @NotNull context: Context,
        title: String = "",
        content: String = "",
        input: String = "",
        cancelAble: Boolean = true,
        mCallback: SingleStringCallback? = null
    ) : this(
        context,
        0x00000001,
        title,
        content,
        input,
        "",
        "",
        "",
        cancelAble,
        mCallback
    )

    constructor(
        @NotNull context: Context,
        title: String = "",
        content: String = "",
        hint: String = "",
        negative: String = "",
        positive: String = "",
        cancelAble: Boolean = true,
        mCallback: SingleStringCallback? = null
    ) : this(
        context,
        0x00000001,
        title,
        content,
        "",
        hint,
        negative,
        positive,
        cancelAble,
        mCallback
    )

    constructor(
        @NotNull context: Context,
        inputType: Int = 0x00000001,
        title: String = "",
        content: String = "",
        cancelAble: Boolean = true,
        mCallback: SingleStringCallback? = null
    ) : this(
        context,
        inputType,
        title,
        content,
        "",
        "",
        "",
        "",
        cancelAble,
        mCallback
    )

    init {

        cet_input.inputType = inputType

        if (!TextUtils.isEmpty(title)) {
            tv_title.text = title
        }

        if (!TextUtils.isEmpty(content)) {
            tv_content.text = content
        }

        if (!TextUtils.isEmpty(input)) {
            cet_input.setText(input)
        }

        if (!TextUtils.isEmpty(hint)) {
            cet_input.hint = hint
        }

        if (!TextUtils.isEmpty(negative)) {
            tv_negative_btn.text = negative
        }

        if (!TextUtils.isEmpty(positive)) {
            tv_positive_btn.text = positive
        }

        setCancelable(cancelAble)
        setCanceledOnTouchOutside(cancelAble)
    }

    override fun setDialogLayout(): Int {
        return R.layout.base_ui_input_dialog_view_layout
    }

    override fun bindView(view: View) {

        tv_negative_btn.setOnClickListener(this)
        tv_positive_btn.setOnClickListener(this)

        AutoLiftUtil.watchKeyboard(mContext,window!!)
    }


    override fun onClick(v: View?) {

        KeyboardUtil.hideKeyboard(v)

        when (v!!.id) {

            R.id.tv_negative_btn -> dismiss()

            R.id.tv_positive_btn -> {
                var res = mCallback?.onPositive(cet_input.text.toString())
                if (res!!) {
                    dismiss()
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        mCallback?.onDismiss()
    }




}
