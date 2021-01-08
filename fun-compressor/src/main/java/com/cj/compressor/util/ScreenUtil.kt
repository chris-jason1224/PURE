package com.cj.compressor.util

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

private fun getDisplayMetrics(context: Context): DisplayMetrics {
    val displayMetrics = DisplayMetrics()
    (context.applicationContext
        .getSystemService(Context.WINDOW_SERVICE) as WindowManager)
        .defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics
}

/**
 * 获取屏幕宽度
 */
fun getScreenWidth(context: Context): Int {
    return getDisplayMetrics(context).widthPixels
}

/**
 * 获取屏幕高度
 */
fun getScreenHeight(context: Context): Int {
    return getDisplayMetrics(context).heightPixels
}