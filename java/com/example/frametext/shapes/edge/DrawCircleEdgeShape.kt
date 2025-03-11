package com.example.frametext.shapes.edge

import android.graphics.Canvas
import android.graphics.Paint

class DrawCircleEdgeShape(width: Int, override var color: Int) : ColoredEdgeShapeDetails {

    override var width: Float = width.toFloat()
    override var height: Float = this.width
    override var centerX: Float = 0.5f * width
    override var centerY: Float = 0.5f * height
    override var bottomAdjustment: Float = -2 * height

    override fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        var yy = y
        yy += height
        canvas.drawArc(x, yy, x + width, yy + height, 0f, 360f, true, paint)
    }
}

