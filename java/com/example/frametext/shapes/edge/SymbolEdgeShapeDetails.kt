package com.example.frametext.shapes.edge

import android.R.attr
import android.graphics.*


class SymbolEdgeShapeDetails(private val symbol: String, val color: Int) : EdgeShapeDetails {
    override var width: Float = 0f
        private set
    override var height: Float = 0f
        private set
    override var centerX: Float = 0f
        private set
    override var centerY: Float = 0f
        private set
    override var bottomAdjustment: Float = -height / 2.0f - centerY
        private set
    override var closestDistance: Int = 150
        private set

    override fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        // draws a blue square behind symbol
        /*
        val col = paint.color
        paint.color = Color.BLUE
        canvas.drawRect(
            x + centerX - width / 2,
            y + centerY - height / 2,
            x + centerX + width / 2,
            y + centerY + height / 2, paint
        )
        paint.color = col
        */
        canvas.drawText(symbol, x, y, paint)
    }

    init {
        val tf = Typeface.create("TimesRoman", Typeface.NORMAL)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.typeface = tf
        paint.textSize = 150f
        val rectBounding = Rect()
        paint.getTextBounds(symbol, 0, symbol.length, rectBounding)

        centerX = rectBounding.exactCenterX()
        centerY = rectBounding.exactCenterY()
        width = rectBounding.width().toFloat()
        height = rectBounding.height().toFloat()
    }
}