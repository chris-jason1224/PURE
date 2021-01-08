package com.cj.ui.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardUtil{

    /**
     * 关闭键盘
     */
    fun hideKeyboard(view: View?): Boolean {
        if (null == view) return false
        val inputManager =
            view.context.applicationContext
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // 即使当前焦点不在editText，也是可以隐藏的。
        return inputManager.hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

}