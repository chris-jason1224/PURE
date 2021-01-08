package com.cj.ui.widget.roundshape

import android.content.Context
import android.util.AttributeSet

/**
 * Author:chris - jason
 * Date:2020-02-04.
 * Package:com.cj.base_ui.view.roundshape
 */


/** 用于需要圆角矩形框背景的TextView的情况,减少直接使用TextView时引入的shape资源文件 */
class RoundTextView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

     var delegate: RoundViewDelegate = RoundViewDelegate(this, context, attrs)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (delegate.isRadiusHalfHeight) {
            delegate.cornerRadius = (height / 2)
        } else {
            delegate.setBgSelector()
        }
    }


}
