package com.cj.ui.util

import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.*

/**
 * 自动托举dialog
 */
object AutoLiftUtil {

    /**
     * 监听软键盘弹出升起、自动上下移动dialog，避免被遮盖住
     * @param mContext Activity
     * @param window dialog.window
     */
    fun watchKeyboard(mContext: Context, window: Window) {

        val activity: AppCompatActivity = mContext as AppCompatActivity

        val activityRoot: View =
            (activity.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup).getChildAt(0)

        val layoutListener: ViewTreeObserver.OnGlobalLayoutListener =
            object : ViewTreeObserver.OnGlobalLayoutListener {
                private val r = Rect()
                private val visibleThreshold = ScreenUtil.dip2px(activity, 150f)
                private var wasOpened = false

                override fun onGlobalLayout() {
                    activityRoot.getWindowVisibleDisplayFrame(r)
                    val heightDiff = activityRoot.rootView.height - r.height()
                    val isOpen = heightDiff > visibleThreshold
                    if (isOpen == wasOpened) {
                        // keyboard state has not changed
                        return
                    }
                    wasOpened = isOpen

                    val listener: KeyboardVisibilityEventListener =
                        object : KeyboardVisibilityEventListener {

                            override fun onVisibilityChanged(
                                isOpen: Boolean,
                                heightDiff: Int
                            ): Boolean {

                                //设置宽高
                                var params: WindowManager.LayoutParams = window.attributes

                                if (isOpen) {
                                    params.y = -ScreenUtil.dip2px(mContext, 100f)
                                } else {
                                    params.y = 0
                                }

                                window.attributes = params

                                return true
                            }

                        }

                    var removeListener = listener!!.onVisibilityChanged(isOpen, heightDiff)

                    if (removeListener) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            activityRoot.viewTreeObserver
                                .removeOnGlobalLayoutListener(this)
                        } else {
                            activityRoot.viewTreeObserver
                                .removeGlobalOnLayoutListener(this)
                        }

                        activityRoot.viewTreeObserver.addOnGlobalLayoutListener(this)
                    }
                }

            }

        activityRoot.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
    }


}

