package com.cj.ui.widget.dialog2
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.view.View
import com.cj.ui.R

import com.cj.ui.widget.dialog2.base.BaseDialogView
import com.cj.ui.widget.dialog2.base.ExtractShareFileCallback
import com.cj.ui.util.AutoLiftUtil
import com.cj.ui.util.KeyboardUtil
import com.cj.ui.util.RandomCodeUtil
import kotlinx.android.synthetic.main.base_ui_import_share_file_dialog_view_layout.*
import org.jetbrains.annotations.NotNull

/**
 * Author:chris - jason
 * Date:2020-02-11.
 * Package:com.cj.base_ui.view.dialog
 * 提取分享文件弹框，提示输入提取码
 */

class ExtractShareFileDialogView @JvmOverloads constructor(
    @NotNull context: Context,
    var fileName:String = "",
    cancelAble: Boolean = true,
    callback: ExtractShareFileCallback? = null
) : BaseDialogView(context) {

    private var mCallback: ExtractShareFileCallback? = callback

    init {
        setCancelable(cancelAble)
        setCanceledOnTouchOutside(cancelAble)

        if(!TextUtils.isEmpty(fileName)){
            if(fileName.length>10){
                fileName = fileName.substring(0,10)+"..."
            }
            tv_content.text = "请输入分享文件【${fileName}】的提取码"
        }
    }

    override fun setDialogLayout(): Int {
        return R.layout.base_ui_import_share_file_dialog_view_layout
    }

    override fun bindView(root: View) {
        iv_code.setOnClickListener(this)

        tv_negative_btn.setOnClickListener(this)
        tv_positive_btn.setOnClickListener(this)

        AutoLiftUtil.watchKeyboard(mContext,window!!)
    }

    override fun onClick(v: View?) {

        KeyboardUtil.hideKeyboard(v)

        when (v!!.id) {
            R.id.iv_code -> iv_code.setImageBitmap(RandomCodeUtil.createBitmap())

            R.id.tv_negative_btn -> {
                mCallback?.onCancel()
                dismiss()
            }


            R.id.tv_positive_btn -> {

                if (TextUtils.isEmpty(cet_input.text.toString())) {
                    var res = mCallback?.onExtractResult(false,"请输入提取码")
                    if (res!!) {
                        dismiss()
                    }
                    return
                }

                if (ll_code.visibility == View.VISIBLE) {
                    if (TextUtils.isEmpty(cet_input_code.text.toString())) {
                        var res = mCallback?.onExtractResult(false,"请输入验证码")
                        if (res!!) {
                            dismiss()
                        }
                        return
                    }


                    if (!cet_input_code.text.toString().equals(RandomCodeUtil.getCode(), ignoreCase = true)) {
                        cet_input_code.setText("")
                        iv_code.setImageBitmap(RandomCodeUtil.createBitmap())
                        var res = mCallback?.onExtractResult(false,"验证码错误")
                        if (res!!) {
                            dismiss()
                        }
                        return
                    }
                }

                var res = mCallback?.onExtractResult(true,cet_input.text.toString())
                if (res!!) {
                    dismiss()
                }

            }
        }

    }

    override fun onDismiss(dialog: DialogInterface?) {
        mCallback?.onDismiss()
    }


    fun afterFail(times:Int){
        cet_input.setText("")
        cet_input_code.setText("")

        if(times<=0){
            ll_code.visibility = View.VISIBLE
            iv_code.setImageBitmap(RandomCodeUtil.createBitmap())
        }else{
            ll_code.visibility = View.GONE
        }

    }

}
