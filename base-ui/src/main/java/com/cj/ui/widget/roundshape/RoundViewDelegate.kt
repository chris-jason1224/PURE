package com.cj.ui.widget.roundshape

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.cj.ui.R

/**
 * Author:chris - jason
 * Date:2020-02-04.
 * Package:com.cj.base_ui.view.roundshape
 */
class RoundViewDelegate(view: View, context: Context, attrs: AttributeSet?) {
    private var view = view
    private var context = context
    private var attrs: AttributeSet? = attrs

    private var gd_background: GradientDrawable = GradientDrawable()
    private var gd_background_press: GradientDrawable = GradientDrawable()

    var backgroundColor = 0
        set(backgroundColor: Int) {
            field = backgroundColor
            setBgSelector()
        }
    var backgroundPressColor = 0
        set(backgroundPressColor: Int) {
            field = backgroundPressColor
            setBgSelector()
        }
    var strokeColor = 0
        set(strokeColor: Int) {
            field = strokeColor
            setBgSelector()
        }
    var strokePressColor = 0
        set(strokePressColor: Int) {
            field = strokePressColor
            setBgSelector()
        }
    var textPressColor = 0
        set(textPressColor: Int) {
            field = textPressColor
            setBgSelector()
        }
    var cornerRadius = 0
        set(cornerRadius: Int) {
            field = cornerRadius
            setBgSelector()
        }
    var cornerRadius_TL = 0
        set(cornerRadius_TL: Int) {
            field = cornerRadius_TL
            setBgSelector()
        }
    var cornerRadius_TR = 0
        set(cornerRadius_TR: Int) {
            field = cornerRadius_TR
            setBgSelector()
        }
    var cornerRadius_BL = 0
        set(cornerRadius_BL: Int) {
            field = cornerRadius_BL
            setBgSelector()
        }
    var cornerRadius_BR = 0
        set(cornerRadius_BR: Int) {
            field = cornerRadius_BR
            setBgSelector()
        }
    var strokeWidth = 0
        set(strokeWidth: Int) {
            field = strokeWidth
            setBgSelector()
        }

    var isRadiusHalfHeight = false
    var isWidthHeightEqual = false
    var isRippleEnable = false
    private var radiusArr = FloatArray(8)

    init {
        obtainAttributes(context, attrs)
    }

    private fun obtainAttributes(context: Context, attrs: AttributeSet?) {

        var ta: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.RoundTextView)
        //背景色
        backgroundColor = ta.getColor(
            R.styleable.RoundTextView_rv_backgroundColor,
            Color.TRANSPARENT
        )
        //按压背景色
        backgroundPressColor = ta.getColor(
            R.styleable.RoundTextView_rv_backgroundPressColor,
            Integer.MAX_VALUE
        )
        //圆角
        cornerRadius =
            ta.getDimensionPixelSize(R.styleable.RoundTextView_rv_cornerRadius, 0)
        //边框宽度
        strokeWidth =
            ta.getDimensionPixelSize(R.styleable.RoundTextView_rv_strokeWidth, 0)
        //边框颜色
        strokeColor =
            ta.getColor(R.styleable.RoundTextView_rv_strokeColor, Color.TRANSPARENT)
        //按压边框颜色
        strokePressColor = ta.getColor(
            R.styleable.RoundTextView_rv_strokePressColor,
            Integer.MAX_VALUE
        )
        //文字按压颜色
        textPressColor = ta.getColor(
            R.styleable.RoundTextView_rv_textPressColor,
            Integer.MAX_VALUE
        )

        isRadiusHalfHeight =
            ta.getBoolean(R.styleable.RoundTextView_rv_isRadiusHalfHeight, false)
        isWidthHeightEqual =
            ta.getBoolean(R.styleable.RoundTextView_rv_isWidthHeightEqual, false)
        cornerRadius_TL = ta.getDimensionPixelSize(
            R.styleable.RoundTextView_rv_cornerRadius_TL,
            0
        )
        cornerRadius_TR = ta.getDimensionPixelSize(
            R.styleable.RoundTextView_rv_cornerRadius_TR,
            0
        )
        cornerRadius_BL = ta.getDimensionPixelSize(
            R.styleable.RoundTextView_rv_cornerRadius_BL,
            0
        )
        cornerRadius_BR = ta.getDimensionPixelSize(
            R.styleable.RoundTextView_rv_cornerRadius_BR,
            0
        )
        //是否启用水波纹动效
        isRippleEnable =
            ta.getBoolean(R.styleable.RoundTextView_rv_isRippleEnable, true)

        ta.recycle()
    }

    private fun setDrawable(gd: GradientDrawable, color: Int, strokeColor: Int) {
        gd.setColor(color)

        if (cornerRadius_TL > 0 || cornerRadius_TR > 0 || cornerRadius_BR > 0 || cornerRadius_BL > 0) {
            //Float数组接收Int元素
            /**The corners are ordered top-left, top-right, bottom-right, bottom-left*/
            radiusArr[0] = cornerRadius_TL.toFloat()
            radiusArr[1] = cornerRadius_TL.toFloat()
            radiusArr[2] = cornerRadius_TR.toFloat()
            radiusArr[3] = cornerRadius_TR.toFloat()
            radiusArr[4] = cornerRadius_BR.toFloat()
            radiusArr[5] = cornerRadius_BR.toFloat()
            radiusArr[6] = cornerRadius_BL.toFloat()
            radiusArr[7] = cornerRadius_BL.toFloat()
            gd.cornerRadii = radiusArr
        } else {
            gd.cornerRadius = cornerRadius.toFloat()
        }

        gd.setStroke(strokeWidth, strokeColor)
    }

     fun setBgSelector() {
        var bg: StateListDrawable = StateListDrawable()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isRippleEnable) {
            setDrawable(gd_background, backgroundColor, strokeColor)
            var rippleDrawable = RippleDrawable(
                getPressedColorSelector(backgroundColor, backgroundPressColor), gd_background, null
            )
            view.background = rippleDrawable
        } else {
            setDrawable(gd_background, backgroundColor, strokeColor)
            bg.addState(intArrayOf(-android.R.attr.state_pressed), gd_background)

            if (backgroundPressColor != Integer.MAX_VALUE || strokePressColor != Integer.MAX_VALUE) {
                setDrawable(
                    gd_background_press,
                    when (backgroundPressColor == Integer.MAX_VALUE) {
                        true -> backgroundColor
                        false -> backgroundPressColor
                    },
                    when (strokePressColor == Integer.MAX_VALUE) {
                        true -> strokeColor
                        false -> strokePressColor
                    }
                )
                bg.addState(intArrayOf(android.R.attr.state_pressed), gd_background_press)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {//16
                view.background = bg
            } else {
                //noinspection deprecation
                view.setBackgroundDrawable(bg)
            }
        }

        if (view is TextView) {
            if (textPressColor != Integer.MAX_VALUE) {
                var textColors: ColorStateList = (view as TextView).getTextColors()
                var colorStateList = ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_pressed),
                        intArrayOf(android.R.attr.state_pressed)
                    ),
                    intArrayOf(textColors.getDefaultColor(), textPressColor)
                )
                (view as TextView).setTextColor(colorStateList)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun getPressedColorSelector(normalColor: Int, pressedColor: Int): ColorStateList {
        return ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_pressed),
                intArrayOf(android.R.attr.state_focused),
                intArrayOf(android.R.attr.state_activated),
                intArrayOf()
            ),
            intArrayOf(pressedColor, pressedColor, pressedColor, normalColor)
        )
    }
}
