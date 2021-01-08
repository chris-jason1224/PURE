package com.cj.ui.util

import android.R
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.os.Environment
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import androidx.annotation.RequiresApi
import java.lang.reflect.Field
import java.util.*

object ScreenUtil {
    /**
     * 屏幕密度,系统源码注释不推荐使用
     */
    val DENSITY =
        Resources.getSystem().displayMetrics.density
    private val TAG = "ScreenUtil"

    /**
     * 是否有摄像头
     */
    private var sHasCamera: Boolean? = null

    /**
     * 获取 DisplayMetrics
     */
    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        (context.applicationContext
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }


    fun getDensity(context: Context): Float {
        return context.resources.displayMetrics.density
    }

    fun getFontDensity(context: Context): Float {
        return context.resources.displayMetrics.scaledDensity
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

    /**
     * 获取屏幕的真实宽高
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun getRealScreenSize(context: Context): IntArray {
        val size = IntArray(2)
        var widthPixels: Int
        var heightPixels: Int
        val w =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val d = w.defaultDisplay
        val metrics = DisplayMetrics()
        d.getMetrics(metrics)
        // since SDK_INT = 1;
        widthPixels = metrics.widthPixels
        heightPixels = metrics.heightPixels
        try {
            // used when 17 > SDK_INT >= 14; includes window decorations (statusbar bar/menu bar)
            widthPixels = Display::class.java.getMethod("getRawWidth").invoke(d) as Int
            heightPixels = Display::class.java.getMethod("getRawHeight").invoke(d) as Int
        } catch (ignored: Exception) {
        }
        try {
            // used when SDK_INT >= 17; includes window decorations (statusbar bar/menu bar)
            val realSize = Point()
            d.getRealSize(realSize)
            Display::class.java.getMethod("getRealSize", Point::class.java)
                .invoke(d, realSize)
            widthPixels = realSize.x
            heightPixels = realSize.y
        } catch (ignored: Exception) {
        }
        size[0] = widthPixels
        size[1] = heightPixels
        return size
    }

    fun isNavMenuExist(context: Context?): Boolean {
        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
        val hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey()
        val hasBackKey =
            KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        return if (!hasMenuKey && !hasBackKey) {
            // 做任何你需要做的,这个设备有一个导航栏
            true
        } else false
    }


    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun sp2px(context: Context, sp: Int): Int {
        return (getFontDensity(context) * sp + 0.5).toInt()
    }

    fun px2sp(context: Context, px: Int): Int {
        return (px / getFontDensity(context) + 0.5).toInt()
    }

    /**
     * 判断是否有状态栏
     */
    fun hasStatusBar(context: Context?): Boolean {
        if (context is Activity) {
            val attrs = context.window.attributes
            return attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN != WindowManager.LayoutParams.FLAG_FULLSCREEN
        }
        return true
    }

    /**
     * 获取ActionBar高度
     */
    fun getActionBarHeight(context: Context): Int {
        var actionBarHeight = 0
        val tv = TypedValue()
        if (context.theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(
                tv.data,
                context.resources.displayMetrics
            )
        }
        return actionBarHeight
    }

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        val c: Class<*>
        val obj: Any
        val field: Field
        val x: Int
        try {
            c = Class.forName("com.android.internal.R\$dimen")
            obj = c.newInstance()
            field = c.getField("status_bar_height")
            x = field[obj].toString().toInt()
            return context.resources
                .getDimensionPixelSize(x)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 获取虚拟菜单的高度,若无则返回0
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun getNavMenuHeight(context: Context): Int {
        if (!isNavMenuExist(context)) {
            return 0
        }
        // 小米4没有nav bar, 而 navigation_bar_height 有值
        val resourceId =
            context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else getRealScreenSize(context)[1] - getScreenHeight(context)

        // 小米 MIX 有nav bar, 而 getRealScreenSize(context)[1] - getScreenHeight(context) = 0
    }

    /**
     * 是否有相机
     */
    fun hasCamera(context: Context): Boolean {
        if (sHasCamera == null) {
            val pckMgr = context.packageManager
            val flag = pckMgr
                .hasSystemFeature("android.hardware.camera.front")
            val flag1 = pckMgr.hasSystemFeature("android.hardware.camera")
            val flag2: Boolean
            flag2 = flag || flag1
            sHasCamera = flag2
        }
        return sHasCamera!!
    }

    /**
     * 是否有物理键盘
     */
    fun hasHardwareMenuKey(context: Context?): Boolean {
        val flag: Boolean
        flag = if (Build.VERSION.SDK_INT < 11) true else if (Build.VERSION.SDK_INT >= 14) {
            ViewConfiguration.get(context).hasPermanentMenuKey()
        } else false
        return flag
    }


    /**
     * 判断 SD Card 是否 ready
     *
     * @return
     */
    fun isSdcardReady(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment
            .getExternalStorageState()
    }

    /**
     * 获取当前国家的语言
     *
     * @param context
     * @return
     */
    fun getCurCountryLan(context: Context): String? {
        val config = context.resources.configuration
        val sysLocale: Locale
        sysLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.locales[0]
        } else {
            config.locale
        }
        return (sysLocale.language
                + "-"
                + sysLocale.country)
    }

    /**
     * 判断是否为中文环境
     *
     * @param context
     * @return
     */
    fun isZhCN(context: Context): Boolean {
        val config = context.resources.configuration
        val sysLocale: Locale
        sysLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.locales[0]
        } else {
            config.locale
        }
        val lang = sysLocale.country
        return lang.equals("CN", ignoreCase = true)
    }

    /**
     * 设置全屏
     *
     * @param context
     */
    fun setFullScreen(context: Context?) {
        if (context is Activity) {
            val activity = context
            val params = activity.window.attributes
            params.flags = params.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
            activity.window.attributes = params
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    /**
     * 取消全屏
     *
     * @param context
     */
    fun cancelFullScreen(context: Context?) {
        if (context is Activity) {
            val activity = context
            val params = activity.window.attributes
            params.flags = params.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            activity.window.attributes = params
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    /**
     * 判断是否全屏
     *
     * @param activity
     * @return
     */
    fun isFullScreen(activity: Activity): Boolean {
        val params = activity.window.attributes
        return params.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN == WindowManager.LayoutParams.FLAG_FULLSCREEN
    }


}