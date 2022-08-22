package com.example.frametext.shapes.edge

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import kotlin.math.*

class DrawSpadeEdgeShape(var size: Int,  override var color: Int) : ColoredEdgeShapeDetails {
    // SquareSide is the side of the square that entirely contains spade.
    // It's the same as height but width is narrower.
    override var height: Float = size.toFloat()
        private set
    override var width: Float = 0.8f * height
        private set
    override var centerX = width / 2.0f
        private set
    override var centerY = height / 2.0f
        private set
    override var bottomAdjustment: Float = -2 * height
        private set

    private val radius: Float = 0.28f * height
    private val path = Path()

    override fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        var yy = y
        yy += height
        val leftXCoordinate = x + (height - height) / 2.0f
        val rightXCoordinate = leftXCoordinate + height
        val leftRect = RectF(
            leftXCoordinate,
            yy + height - 2.5f * radius,
            leftXCoordinate + 2 * radius,
            yy + height - 0.5f * radius
        )
        val rightRect = RectF(
            rightXCoordinate - 2 * radius,
            yy + height - 2.5f * radius,
            rightXCoordinate,
            yy + height - 0.5f * radius
        )
        val leftBottomRect = RectF(
            x + height / 2.0f - 2 * radius,
            yy + height - 2f * radius,
            x + height / 2.0f,
            yy + height
        )
        val rightBottomRect = RectF(
            x + height / 2.0f,
            yy + height - 2f * radius,
            x + height / 2.0f + 2 * radius,
            yy + height
        )
        path.reset()
        path.moveTo(x + 0.5f * height, yy)
        path.arcTo(
            leftRect, beta,
            alpha - beta
        )
        path.arcTo(rightRect, -alpha + 180, -(-alpha + beta))
        path.lineTo(x + 0.5f * height, yy)

        // Draw bottom tongue
        path.moveTo(x + height / 2.0f, yy + height - radius)
        path.arcTo(leftBottomRect, 0f, 90f)
        path.lineTo(x + height / 2.0f + radius, yy + height)
        path.arcTo(rightBottomRect, 90f, 90f)
        canvas.drawPath(path, paint)
    }

    companion object {
        private var alphaBetaComputed = false
        private var alpha = 0f
        private var beta = 0f
        fun initializeAlphaBeta(_width: Float, _height: Float, radius: Float) {
            if (!alphaBetaComputed) {
                alphaBetaComputed = true
                // Start angle of left heart curve is given by:
                alpha = (acos((_width / 2.0 - radius) / radius) * 180 / Math.PI).toFloat()
                val phi = atan((_height - 1.5 * radius) / (_width / 2.0 - radius))
                // distance l between centre of heart (Cx, Cy) and bottom point of heart (BPx, BPy) is
                // SQRT((PBx - Cx)pow2 + (BPy - Cy)pow2)
                // Cx = x + radius
                // Cy = y + radius
                // BPx = x + _width/2.0
                // PBy = y + _height
                // this is:
                // l = Math.sqrt(Math.pow(x + _width/2.0 - (x + radius), 2) + Math.pow(y + _height - (y + radius), 2));
                // Which can be simplified into:
                val l = sqrt(
                    (_width / 2.0 - radius).pow(2.0) + (_height - 1.5 * radius).pow(2.0)
                )
                val zeta = acos(radius / l)
                val betaRadian = max(phi + zeta, phi - zeta)
                beta = (betaRadian * 180.0 / Math.PI).toFloat() + 90
            }
        }
    }

    init {
        initializeAlphaBeta(width, height, radius)
    }
}
