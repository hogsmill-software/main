package com.example.frametext.shapes.edge

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.cos
import kotlin.math.sin

class DrawSmileyEdgeShape(side: Int, override var color: Int) : ColoredEdgeShapeDetails {

    override var width: Float = side.toFloat()
    override var height: Float = side.toFloat()
    override var centerX: Float = 0.5f * width
    override var centerY: Float = 0.5f * height
    override var bottomAdjustment: Float = -2 * height

    override fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        var yy = y
        yy += height
        val path = Path()
        // draw face disc
        path.addCircle(x + centerX, yy + centerY, 0.5f * height, Path.Direction.CCW)

        // draw eyes
        val eyeRadius = height / 9.0f
        val eyeHeight = 0.65f * height
        val eyeLeftRightDisplacement = 0.19f * height
        path.addCircle(
            x + centerX - eyeLeftRightDisplacement,
            yy + height - eyeHeight,
            eyeRadius,
            Path.Direction.CW
        )
        path.addCircle(
            x + centerX + eyeLeftRightDisplacement,
            yy + height - eyeHeight,
            eyeRadius,
            Path.Direction.CW
        )

        // draw mouth
        val lowerLipCircleCentre = 0.44f * height
        val lowerLipRadius = 0.255f * height
        val smileAngle = 10f
        val rightAngleRad = smileAngle * Math.PI / 180
        path.moveTo(
            x + centerX + (lowerLipRadius * cos(rightAngleRad)).toFloat(),
            yy + height - lowerLipCircleCentre + (lowerLipRadius * sin(rightAngleRad)).toFloat()
        )
        path.addArc(
            x + centerX - lowerLipRadius,
            yy + height - lowerLipCircleCentre - lowerLipRadius,
            x + centerX + lowerLipRadius,
            yy + height - lowerLipCircleCentre + lowerLipRadius,
            smileAngle,
            180 - 2 * smileAngle
        )
        val leftAngleRad = (180 - smileAngle) * Math.PI / 180
        path.lineTo(
            x + centerX + (lowerLipRadius * cos(leftAngleRad)).toFloat(),
            yy + height - lowerLipCircleCentre + (lowerLipRadius * sin(leftAngleRad)).toFloat()
        )
        val upperLipCircleCentre = 0.473f * height
        val upperLipRadius = 0.207f * height
        path.lineTo(
            x + centerX + (upperLipRadius * cos(leftAngleRad)).toFloat(),
            yy + height - upperLipCircleCentre + (upperLipRadius * sin(leftAngleRad)).toFloat()
        )
        path.addArc(
            x + centerX - upperLipRadius,
            yy + height - upperLipCircleCentre - upperLipRadius,
            x + centerX + upperLipRadius,
            yy + height - upperLipCircleCentre + upperLipRadius,
            180 - smileAngle,
            -(180 - 2 * smileAngle)
        )
        path.lineTo(
            x + centerX + (upperLipRadius * cos(rightAngleRad)).toFloat(),
            yy + height - upperLipCircleCentre + (upperLipRadius * sin(rightAngleRad)).toFloat()
        )
        path.lineTo(
            x + centerX + (lowerLipRadius * cos(rightAngleRad)).toFloat(),
            yy + height - lowerLipCircleCentre + (lowerLipRadius * sin(rightAngleRad)).toFloat()
        )
        canvas.drawPath(path, paint)
    }
}