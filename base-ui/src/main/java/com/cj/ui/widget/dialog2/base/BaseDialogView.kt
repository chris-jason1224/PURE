package com.cj.ui.widget.dialog2.base

/**
 * Author:chris - jason
 * Date:2020-02-04.
 * Package:com.cj.base_ui.view.dialog.base
 */
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.*
import com.cj.ui.R


/**
 * Author:chris - jason
 * Date:2019-06-27.
 * Package:com.cj.ui.dialog.view
 * 通用Dialog弹窗基类
 */
abstract class BaseDialogView(mContext: Context) : Dialog(mContext, R.style.base_ui_common_dialog),
    View.OnClickListener, DialogInterface.OnDismissListener{

    var mContext: Context = mContext
    lateinit var mContentView: View

    init {
        initAttr()
    }

    //初始化基础属性
    private fun initAttr() {

        //注册dismiss、cancel监听
        setOnDismissListener(this)

        var inflater: LayoutInflater = LayoutInflater.from(mContext)
        var windowView: View = inflater.inflate(setDialogLayout(), null)

        setCancelable(true);//点击外部区域可取消
        setCanceledOnTouchOutside(true);//点击返回键可取消

        //set view
        super.setContentView(windowView)
        this.mContentView = windowView

        var window: Window? = window
        window?.setGravity(Gravity.CENTER)//默认显示在中间，子类自行覆盖属性

        //设置宽高
        var params: WindowManager.LayoutParams = window!!.attributes
        var display: Display = window.windowManager.defaultDisplay

        params.width = display.width
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = params

        //bind view
        bindView(windowView)
    }

    abstract fun setDialogLayout(): Int

    abstract fun bindView(root: View)

//    override fun show() {
//        // Dialog 在初始化时会生成新的 Window，先禁止 Dialog Window 获取焦点，
//        // 等 Dialog 显示后,对 Dialog Window 的 DecorView 设置 setSystemUiVisibility ，
//        // 接着再获取焦点。 这样表面上看起来就没有退出沉浸模式。
//        // Set the dialog to not focusable (makes navigation ignore us adding the window)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//        )
//
//        //Show the dialog!
//        super.show()
//
//        //Set the dialog to immersive
//        fullScreenImmersive(window.decorView)
//
//        //Clear the not focusable flag from the window
//        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
//    }

    /**
     * 全屏显示，隐藏虚拟按钮
     * @param view
     */
    private fun fullScreenImmersive(view: View) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            var uiOptions: Int =
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_FULLSCREEN
            view.systemUiVisibility = uiOptions
        }
    }


}
