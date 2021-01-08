package com.cj.ui.widget.dialog2

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.RadioGroup
import com.cj.ui.R
import com.cj.ui.widget.dialog2.base.BaseDialogView
import com.cj.ui.widget.dialog2.base.DoubleBtn_RadioButton_Callback
import com.cj.ui.util.AutoLiftUtil
import com.cj.ui.util.KeyboardUtil
import kotlinx.android.synthetic.main.base_ui_share_file_dialog_view_layout.*
import org.jetbrains.annotations.NotNull
import java.util.*


/**
 * Author:chris - jason
 * Date:2020-02-04.
 * Package:com.cj.base_ui.view.dialog
 * 分享文件时弹框，供用户编辑提取码、选择提取有效期
 */
class ShareFileDialogView @JvmOverloads constructor(
    @NotNull context: Context,
    cancelAble: Boolean = true,
    mCallback: DoubleBtn_RadioButton_Callback? = null
) : BaseDialogView(context) {

    private var mCallback: DoubleBtn_RadioButton_Callback? = mCallback
    private var validDate = 1
    private var extractTimes = ""

    init {
        setCancelable(cancelAble)
        setCanceledOnTouchOutside(cancelAble)
    }

    override fun setDialogLayout(): Int {
        return R.layout.base_ui_share_file_dialog_view_layout
    }

    override fun bindView(view: View) {

        //生成随机提取码
        cet_input.setText(getRandomString(4))

        rg_choice_time.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_week -> {
                    validDate = 7
                    extractTimes = ""
                }
                R.id.rb_day -> {
                    validDate = 1
                    extractTimes = ""
                }
                R.id.rb_once -> {
                    extractTimes = "oneTimes"
                }
                else -> {
                    validDate = 1
                    extractTimes = ""
                }
            }
        })

        tv_negative_btn.setOnClickListener(this)
        tv_positive_btn.setOnClickListener(this)

        AutoLiftUtil.watchKeyboard(mContext,window!!)
    }

    override fun onClick(v: View?) {

        KeyboardUtil.hideKeyboard(v)

        when (v!!.id) {

            R.id.tv_negative_btn -> dismiss()

            R.id.tv_positive_btn -> {
                mCallback?.onPositive(cet_input.text.toString(),validDate, extractTimes)
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        mCallback?.onDismiss()
    }

    private fun getRandomString(stringLength: Int): String? {
        val string = "abcdefghijklmnopqrstuvwxyz"
        val sb = StringBuilder()
        for (i in 0 until stringLength - 2) {
            val index =
                Math.floor(Math.random() * (string.length - 2)).toInt()
            sb.append(string[index])
        }
        val random = Random()
        val ends = random.nextInt(99)
        val format = String.format("%02d", ends)
        sb.append(format)
        return sb.toString()
    }


}
