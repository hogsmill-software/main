package com.example.frametext.shapes.main

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path

class SquareMainShape(private var _color: Int, _size: Int) :
    MainShape {
    var size: Float = 0.0f
    private val path = Path()

    override fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        var yy = y
        yy += size
        canvas.drawRect(x, yy, x + size, yy + size, paint)
        path.reset()
        path.addRect(x, yy, x + size, yy + size, Path.Direction.CCW)
    }

    override fun getWidth(): Float {
        return size
    }

    override fun getHeight(): Float {
        return size
    }

    override fun getColor(): Int {
        return _color
    }

    override fun setColor(color: Int) {
        _color = color
    }

    override fun drawWriting(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        val line = Path()
        line.moveTo(x, 0f)
        line.lineTo(x + size, 0f)
        val vLineShift = 3
        line.moveTo(x, size / 6f + vLineShift)
        line.lineTo(x + size, size / 6f + vLineShift)
        line.moveTo(x, 2 * size / 6f + vLineShift)
        line.lineTo(x + size, 2 * size / 6f + vLineShift)
        line.moveTo(x, 3 * size / 6f + vLineShift)
        line.lineTo(x + size, 3 * size / 6f + vLineShift)
        line.moveTo(x, 4 * size / 6f + vLineShift)
        line.lineTo(x + size, 4 * size / 6f + vLineShift)
        line.moveTo(x, 5 * size / 6f + vLineShift)
        line.lineTo(x + size, 5 * size / 6f + vLineShift)
        line.moveTo(x, 6 * size / 6f + vLineShift)
        line.lineTo(x + size, 6 * size / 6f + vLineShift)

        canvas.save()
        canvas.clipPath(path)
        canvas.drawPath(line, paint)
        canvas.restore()
    }

    init {
        size = _size.toFloat()
    }
}