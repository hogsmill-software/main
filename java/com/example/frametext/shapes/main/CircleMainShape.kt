package com.example.frametext.shapes.main

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path

class CircleMainShape(private var color: Int, width: Int) : MainShape {
    private val width: Float = width.toFloat()
    override fun getWidth(): Float {
        return width
    }

    private var height = 0f

    override fun getHeight(): Float {
        return height
    }

    override fun getColor(): Int {
        return color
    }

    override fun setColor(color: Int) {
        this.color = color
    }

    private fun initialize() {
        height = width
    }

    override fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        var yy = y
        yy += height
        canvas.drawArc(x, yy, x + width, yy + height, 0f, 360f, true, paint)
    }

    override fun drawWriting(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        val line = Path()
        line.moveTo(x, 0f)
        line.lineTo(x + width, 0f)
        val vLineShift = 3
        line.moveTo(x, height / 6f + vLineShift)
        line.lineTo(x + width, height / 6f + vLineShift)
        line.moveTo(x, 2 * height / 6f + vLineShift)
        line.lineTo(x + width, 2 * height / 6f + vLineShift)
        line.moveTo(x, 3 * height / 6f + vLineShift)
        line.lineTo(x + width, 3 * height / 6f + vLineShift)
        line.moveTo(x, 4 * height / 6f + vLineShift)
        line.lineTo(x + width, 4 * height / 6f + vLineShift)
        line.moveTo(x, 5 * height / 6f + vLineShift)
        line.lineTo(x + width, 5 * height / 6f + vLineShift)
        line.moveTo(x, 6 * height / 6f + vLineShift)
        line.lineTo(x + width, 6 * height / 6f + vLineShift)
        canvas.save()
        val path = Path()
        var yy = y
        yy += height
        path.addArc(x, yy, x + width, yy + height, 0f, 360f)
        canvas.clipPath(path)
        canvas.drawPath(line, paint)
        canvas.restore()
    }

    init {
        initialize()
    }
}