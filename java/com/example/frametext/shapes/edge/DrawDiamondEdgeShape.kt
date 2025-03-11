package com.example.frametext.shapes.edge

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path

class DrawDiamondEdgeShape(size: Int, override var color: Int) : ColoredEdgeShapeDetails {
    // SquareSide is the side of the square that entirely contains square.
    // It's the same as height but width is narrower.
    override var height: Float = size.toFloat()
    override var width: Float = 0.7f * height
    override var centerX: Float = width / 2.0f
    override var centerY: Float = height / 2.0f
    override var bottomAdjustment: Float = -2 * height

    private val path = Path()

    override fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        var yy = y
        yy += height
        path.reset()
        path.moveTo(x + 0.5f * height, yy)
        path.lineTo(x + 0.5f * (height - height), yy + 0.5f * height)
        path.lineTo(x + 0.5f * height, yy + height)
        path.lineTo(x + 0.5f * (height + height), yy + 0.5f * height)
        path.lineTo(x + 0.5f * height, yy)
        canvas.drawPath(path, paint)
    }
}
