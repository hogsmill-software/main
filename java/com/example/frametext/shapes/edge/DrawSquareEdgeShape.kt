package com.example.frametext.shapes.edge

import android.graphics.Canvas
import android.graphics.Paint

class DrawSquareEdgeShape(_size: Int, override var color: Int) :
    ColoredEdgeShapeDetails {
    private var size: Float = 0.0f

    override var centerX = 0f
        private set
    override var centerY = 0f
        private set
    override var width = 0f
        private set
    override var height = 0f
        private set

    override fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        var yy = y
        yy += size
        canvas.drawRect(x, yy, x + size, yy + size, paint)
    }

    override val bottomAdjustment: Float
        get() = -2 * size

    init {
        size = _size.toFloat()
        centerX = 0.5f * size
        centerY = 0.5f * size
        width = size
        height = size
    }
}