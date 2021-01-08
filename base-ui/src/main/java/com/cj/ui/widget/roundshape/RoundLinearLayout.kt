package com.cj.ui.widget.roundshape

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

/**
 * Author:chris - jason
 * Date:2020-02-04.
 * Package:com.cj.base_ui.view.roundshape
 */


/** 用于需要圆角矩形框背景的LinearLayout的情况,减少直接使用LinearLayout时引入的shape资源文件 */
class RoundLinearLayout(context: Context, attributeSet: AttributeSet?) :
    LinearLayout(context, attributeSet) {

    var delegate = RoundViewDelegate(this, context, attributeSet)

    constructor(context: Context) : this(context, null)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (delegate.isWidthHeightEqual && width > 0 && height > 0) {
            var max = width.coerceAtLeast(height)
            var measureSpec = MeasureSpec.makeMeasureSpec(max, MeasureSpec.EXACTLY)
            super.onMeasure(measureSpec, measureSpec)
            return
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (delegate.isRadiusHalfHeight) {
            delegate.cornerRadius = (height / 2)
        } else {
            delegate.setBgSelector()
        }
    }

}
