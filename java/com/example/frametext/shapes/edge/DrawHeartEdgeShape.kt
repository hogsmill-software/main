package com.example.frametext.shapes.edge

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import kotlin.math.*

class DrawHeartEdgeShape(
    heartWidth: Int, override var color: Int
) : ColoredEdgeShapeDetails {
    override var width: Float = heartWidth.toFloat()
    override var height: Float = 0.96f * width
    override var centerX: Float = width / 2.0f
    override var centerY: Float = height / 2.0f
    override var bottomAdjustment: Float = -2 * height

    private var radius = 0.28f * width
    private val path = Path()
    private fun initialize() {
        // heartWidth = 92;
        height = 0.96f * width
        radius = 0.28f * width
        centerX = 0.5f * width
        centerY = 0.5f * height
        initializeAlphaBeta(width, height, radius)
    }

    override fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        var yy = y
        yy += height
        val leftRect = RectF(x, yy, x + 2 * radius, yy + 2 * radius)
        val rightRect = RectF(x + width - 2 * radius, yy, x + width, yy + 2 * radius)

        // Scaffolding code below to help with final drawing
        // Let us draw the background that contains heart
        /* paint.setColor(Color.GRAY);
        RectF boundaryRect = new RectF(x, y, x + width, y + height);
        canvas.drawRect(boundaryRect, paint);

        paint.setColor(Color.BLUE);

        canvas.drawRect(leftRect, paint);

        paint.setColor(Color.MAGENTA);
        RectF leftSideRect = new RectF(x, y, x + width/2f, y + height);
        canvas.drawRect(leftSideRect, paint);

        paint.setColor(Color.CYAN);

        canvas.drawArc(leftRect, -alpha, -beta + alpha, true, paint);


        paint.setColor(Color.YELLOW);

        canvas.drawArc(rightRect, beta + 180, -beta + alpha, true, paint);
        paint.setColor(Color.GREEN);
        float startEndPtX = x + 0.5f * width;
        float startEndPtY = y + radius - (float)Math.sqrt(Math.pow(radius, 2) - Math.pow(width/2.0 - radius, 2));
        path.moveTo(startEndPtX, startEndPtY);
        */path.reset()
        //	paint.setColor(Color.RED);
        path.arcTo(leftRect, -alpha, -beta + alpha)
        path.lineTo(x + 0.5f * width, yy + height)
        path.arcTo(rightRect, beta + 180, -beta + alpha)
        canvas.drawPath(path, paint)
    }

    companion object {
        private var alphaBetaComputed = false
        private var alpha = 0f
        private var beta = 0f
        fun initializeAlphaBeta(width: Float, height: Float, radius: Float) {
            if (!alphaBetaComputed) {
                alphaBetaComputed = true
                // Start angle of left heart curve is given by:
                alpha = (acos((width / 2.0 - radius) / radius) * 180 / Math.PI).toFloat()
                val phi = atan((height - radius) / (width / 2.0 - radius))
                // distance l between centre of heart (Cx, Cy) and bottom point of heart (BPx, BPy) is
                // SQRT((PBx - Cx)pow2 + (BPy - Cy)pow2)
                // Cx = x + radius
                // Cy = y + radius
                // BPx = x + width/2.0
                // PBy = y + height
                // this is:
                // l = Math.sqrt(Math.pow(x + width/2.0 - (x + radius), 2) + Math.pow(y + height - (y + radius), 2));
                // Which can be simplified into:
                val l = sqrt(
                    (width / 2.0 - radius).pow(2.0) + (height - radius).toDouble().pow(2.0)
                )
                val zeta = acos(radius / l)
                val betaRadian = max(phi + zeta, phi - zeta)
                beta = (betaRadian * 180.0 / Math.PI).toFloat() + 90
            }
        }
    }

    init {
        width = heartWidth.toFloat()
        initialize()
    }
}
