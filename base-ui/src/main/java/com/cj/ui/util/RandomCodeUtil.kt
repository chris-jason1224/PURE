package com.cj.ui.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.util.*

object RandomCodeUtil {


    //随机数数组
    private val CHARS = charArrayOf(
        '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm',
        'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    )

    //验证码默认随机数的个数
    private val DEFAULT_CODE_LENGTH = 4

    //默认字体的大小
    private val DEFAULT_FONT_SIZE = 25

    //默认线条的个数
    private val DEFAULT_LINE_NUMBER = 5

    //padding值
    private val BASE_PADDING_LEFT = 10  //padding值
    private val RANGE_PADDING_LEFT = 15  //padding值
    private val BASE_PADDING_TOP = 15 //padding值
    private val RANGE_PADDING_TOP = 20

    //验证码的默认宽高
    private val DEFAULT_WITH = 100  //验证码的默认宽高
    private val DEFAULT_HEIGHT = 40

    //画布的宽高
    private val with = DEFAULT_WITH //画布的宽高
    private val height = DEFAULT_HEIGHT

    //random word space and pading_top
    private val base_padding_left = BASE_PADDING_LEFT  //random word space and pading_top
    private val range_padding_left = RANGE_PADDING_LEFT  //random word space and pading_top
    private val base_padding_top = BASE_PADDING_TOP  //random word space and pading_top
    private val range_padding_top = RANGE_PADDING_TOP

    //字符、线条、的个数，字体的大小
    private val codeLength = DEFAULT_CODE_LENGTH  //字符、线条、的个数，字体的大小
    private val line_number = DEFAULT_LINE_NUMBER  //字符、线条、的个数，字体的大小
    private val font_size = DEFAULT_FONT_SIZE

    //变量
    private var code: String? = null
    private var padding_left = 0
    private var padding_top: Int = 0
    private val random = Random()



    //验证码图片
    fun createBitmap(): Bitmap? {
        padding_left = 0
        val bp = Bitmap.createBitmap(with, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bp)
        code = createCode()
        c.drawColor(Color.GRAY) //设置画布的颜色
        val paint = Paint()
        paint.isAntiAlias = true //去除锯齿
        paint.textSize = font_size.toFloat()
        //画验证码
        for (i in 0 until code!!.length) {
            randomTextStyle(paint)
            randomPadding()
            c.drawText(
                code!![i].toString() + "",
                padding_left.toFloat(),
                padding_top.toFloat(),
                paint
            )
        }
        //画线条
        for (i in 0 until line_number) {
            drawLine(c, paint)
        }
        c.save()
        c.restore()
        return bp
    }

    fun getCode(): String? {
        return code
    }

    //画干扰线
    private fun drawLine(
        canvas: Canvas,
        paint: Paint
    ) {
        val color = randomColor()
        val startX = random.nextInt(with)
        val startY = random.nextInt(height)
        val stopX = random.nextInt(with)
        val stopY = random.nextInt(height)
        paint.strokeWidth = 1f
        paint.color = color
        canvas.drawLine(startX.toFloat(), startY.toFloat(), stopX.toFloat(), stopY.toFloat(), paint)
    }

    //生成随机颜色
    private fun randomColor(): Int {
        return randomColor(1)
    }

    private fun randomColor(rate: Int): Int {
        val red = random.nextInt(256) / rate
        val green = random.nextInt(256) / rate
        val blue = random.nextInt(256) / rate
        return Color.rgb(red, green, blue)
    }

    //随机生成padding值
    private fun randomPadding() {
        padding_left += base_padding_left + random.nextInt(range_padding_left)
        padding_top = base_padding_top + random.nextInt(range_padding_top)
    }

    //随机生成文字样式，颜色，粗细，倾斜度
    private fun randomTextStyle(paint: Paint) {
        val color = randomColor()
        paint.color = color
        paint.isFakeBoldText = random.nextBoolean() //true为粗体，false为非粗体
        var skewX = random.nextInt(11) / 10.toFloat()
        skewX = if (random.nextBoolean()) skewX else -skewX
        paint.textSkewX = skewX //float类型参数，负数表示右斜，整数左斜
    }

    //生成验证码
    private fun createCode(): String? {
        val buffer = StringBuilder()
        for (i in 0 until codeLength) {
            buffer.append(CHARS[random.nextInt(CHARS.size)])
        }
        return buffer.toString()
    }
}