package com.example.frametext.shapes.edge

import android.graphics.Canvas
import android.graphics.Paint

interface EdgeShapeDetails {
    fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint)
    val width: Float
    val height: Float
    val centerX: Float
    val centerY: Float
    val bottomAdjustment: Float
    val closestDistance: Int
}