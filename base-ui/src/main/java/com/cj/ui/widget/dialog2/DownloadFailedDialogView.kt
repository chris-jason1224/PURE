package com.cj.ui.widget.dialog2
import android.content.Context
import android.content.DialogInterface
import android.view.View
import com.cj.ui.R
import com.cj.ui.widget.dialog2.base.BaseDialogView
import org.jetbrains.annotations.NotNull

/**
 * Author:chris - jason
 * Date:2020-02-11.
 * Package:com.cj.base_ui.view.dialog
 * 确认对话框 标题+内容+两个按钮
 */

class DownloadFailedDialogView @JvmOverloads constructor(
    @NotNull context: Context
) : BaseDialogView(context) {

    init {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    override fun setDialogLayout(): Int {
        return R.layout.base_ui_download_failed_dialog_view_layout
    }

    override fun bindView(root: View) {

    }

    override fun onClick(v: View?) {

    }

    override fun onDismiss(dialog: DialogInterface?) {

    }


}
