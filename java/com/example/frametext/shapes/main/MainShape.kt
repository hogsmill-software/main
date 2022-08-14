package com.example.frametext.shapes.main

import android.graphics.Canvas
import android.graphics.Paint

interface MainShape {
    fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint)

    fun getWidth(): Float
    fun getHeight(): Float

    fun getColor(): Int
    fun setColor(color: Int)

    // Draws lines inside shape so looks like shape has text written inside
    fun drawWriting(canvas: Canvas, x: Float, y: Float, paint: Paint)
}