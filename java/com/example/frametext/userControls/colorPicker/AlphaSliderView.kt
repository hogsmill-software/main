package com.example.frametext.userControls.colorPicker

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet

class AlphaSliderView : ColorSliderView {
    private var backgroundBitmap: Bitmap? = null
    private var backgroundCanvas: Canvas? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        backgroundBitmap = Bitmap.createBitmap(
            (w - 2 * selectorSize).toInt(),
            (h - selectorSize).toInt(), Bitmap.Config.ARGB_8888
        )
        backgroundBitmap?.let {
            backgroundCanvas = Canvas(it)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val drawable: Drawable = CheckerboardDrawable.create()
        drawable.setBounds(0, 0, width, height)
        backgroundCanvas?.let {
            drawable.draw(it)
        }
        backgroundBitmap?.let {
            canvas.drawBitmap(it, selectorSize, selectorSize, null)
        }
        super.onDraw(canvas)
    }

    override fun resolveValue(color: Int): Float {
        return Color.alpha(color) / 255f
    }

    override fun configurePaint(colorPaint: Paint?) {
        val hsv = FloatArray(3)
        Color.colorToHSV(baseColor, hsv)
        val startColor = Color.HSVToColor(0, hsv)
        val endColor = Color.HSVToColor(255, hsv)

        val shader: Shader = LinearGradient(
            0f, 0f,
            width.toFloat(), height.toFloat(), startColor, endColor, Shader.TileMode.CLAMP
        )


        colorPaint?.let {
            colorPaint.shader = shader
        }
    }

    override fun assembleColor(): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(baseColor, hsv)
        val alpha = (currentValue * 255).toInt()
        return Color.HSVToColor(alpha, hsv)
    }
}
