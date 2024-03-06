package com.example.frametext.userControls.colorPicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

class ColorWheelSelector @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    private val selectorPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var selectorRadiusPx = (Constants.SELECTOR_RADIUS_DP * 3).toFloat()
    private var currentPoint = PointF()
    override fun onDraw(canvas: Canvas) {
        canvas.drawLine(
            currentPoint.x - selectorRadiusPx, currentPoint.y,
            currentPoint.x + selectorRadiusPx, currentPoint.y, selectorPaint
        )
        canvas.drawLine(
            currentPoint.x, currentPoint.y - selectorRadiusPx,
            currentPoint.x, currentPoint.y + selectorRadiusPx, selectorPaint
        )
        canvas.drawCircle(currentPoint.x, currentPoint.y, selectorRadiusPx * 0.66f, selectorPaint)
    }

    fun setSelectorRadiusPx(selectorRadiusPx: Float) {
        this.selectorRadiusPx = selectorRadiusPx
    }

    fun setCurrentPoint(currentPoint: PointF) {
        this.currentPoint = currentPoint
        invalidate()
    }

    init {
        // Black not ideal in dark mode but other colours are even worse
      //  selectorPaint.color = if (context != null) ContextCompat.getColor(context, Utilities.getTextColorId(context)) else android.graphics.Color.BLACK
        selectorPaint.color = android.graphics.Color.BLACK
        selectorPaint.style = Paint.Style.STROKE
        selectorPaint.strokeWidth = 2f
    }
}
