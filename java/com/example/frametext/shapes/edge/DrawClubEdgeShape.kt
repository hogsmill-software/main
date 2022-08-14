package com.example.frametext.shapes.edge

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import kotlin.math.acos

class DrawClubEdgeShape(size: Int, override var color: Int) : ColoredEdgeShapeDetails {
    // SquareSide is the side of the square that entirely contains spade.
    // It's the same as height but width is narrower.
    val size: Float

    // private final float width;
    private val radius: Float

    // stemLen is the distance of the stem joining the 3 leaves
    private val stemLen: Float
    private val halfStemThickness: Float
    private val path = Path()

    override var width: Float = size.toFloat()
        private set
    override var height: Float = size.toFloat()
        private set
    override var centerX: Float = size / 2.0f
        private set
    override var centerY: Float = size / 2.0f
        private set
    override var bottomAdjustment: Float = -2 * size.toFloat()
        private set
    override var closestDistance: Int = 150
        private set

    override fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        var yy = y
        yy += size
        val topRect =
            RectF(x + size / 2.0f - radius, yy, x + size / 2.0f + radius, yy + 2f * radius)
        val leftRect = RectF(
            x,
            yy + size - stemLen - radius,
            x + 2.0f * radius,
            yy + size - stemLen + radius
        )
        val rightRect = RectF(
            x + size - 2 * radius,
            yy + size - stemLen - radius,
            x + size,
            yy + size - stemLen + radius
        )
        val leftBottomRect = RectF(
            x + size / 2.0f - 2 * radius - halfStemThickness,
            yy + size - 2 * stemLen,
            x + size / 2.0f - halfStemThickness,
            yy + size
        )
        val rightBottomRect = RectF(
            x + size / 2.0f + halfStemThickness,
            yy + size - 2 * stemLen,
            x + size / 2.0f + 2 * radius + halfStemThickness,
            yy + size
        )
        paint.isAntiAlias = true
        path.reset()

        // Draw top leaf
        path.moveTo(x + size / 2.0f, yy + size - stemLen)
        path.arcTo(topRect, topStartAngle, topSweepAngle)
        path.lineTo(x + size / 2.0f, yy + size - stemLen)
        canvas.drawPath(path, paint)
        path.reset()

        // Draw left leaf
        path.addCircle(leftRect.centerX(), leftRect.centerY(), radius, Path.Direction.CW)
        canvas.drawPath(path, paint)
        path.reset()

        // Draw right leaf
        path.addCircle(rightRect.centerX(), rightRect.centerY(), radius, Path.Direction.CW)
        canvas.drawPath(path, paint)
        path.reset()

        // Draw stem
        path.addRect(
            x + size / 2.0f - halfStemThickness,
            yy + size - stemLen - 4 * halfStemThickness,
            x + size / 2.0f + halfStemThickness,
            yy + size,
            Path.Direction.CCW
        )
        // Draw bottom tongue
        path.moveTo(x + size / 2.0f, yy + size - stemLen)
        path.arcTo(leftBottomRect, 0f, 90f)
        path.lineTo(x + size / 2.0f + radius, yy + size)
        path.arcTo(rightBottomRect, 90f, 90f)
        canvas.drawPath(path, paint)
    }

    companion object {
        private var anglesComputed = false
        private var topStartAngle = 0f
        private var topSweepAngle = 0f
        private fun getAngle(radius: Float, dist: Float): Float {
            return (acos((radius / dist).toDouble()) * 180 / Math.PI).toFloat()
        }

        private fun initializeAngles(dcd: DrawClubEdgeShape) {
            if (!anglesComputed) {
                anglesComputed = true
                // top angles
                val dist = dcd.size - dcd.stemLen - dcd.radius
                val topAngles = getAngle(dcd.radius, dist)
                topStartAngle = topAngles + 90f
                topSweepAngle = 360 - 2 * topAngles
            }
        }
    }

    init {
        this.size = size.toFloat()
        radius = 0.24f * size
        stemLen = 0.3f * size
        halfStemThickness = 0.02f * size
        initializeAngles(this)
    }
}
