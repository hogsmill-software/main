package com.example.frametext.shapes.edge

import android.graphics.PointF
import kotlin.math.cos
import kotlin.math.sin

class StarDetailsGlobalObject internal constructor(searchDetails: StarSearchDetails) {
    val centerX: Float
    val centerY: Float
    val starPtsList = ArrayList<PointF>()

    init {
        centerX = 0.5f * searchDetails.widthHeight
        centerY = 0.5f * searchDetails.widthHeight
        val innerRadius: Float = searchDetails.innerRadius
        val outerRadius: Float = 0.5f * searchDetails.widthHeight
        val pt = PointF(
            (centerX + outerRadius * cos(searchDetails.startAngle)).toFloat(),
            (centerY + outerRadius * sin(searchDetails.startAngle)).toFloat()
        )
        starPtsList.add(pt)
        val forwardAngleMove: Double = Math.PI / searchDetails.spikes
        var currentAngle: Double = searchDetails.startAngle
        while (currentAngle < searchDetails.startAngle + 2 * Math.PI) {
            currentAngle += forwardAngleMove
            starPtsList.add(
                PointF(
                    (centerX + innerRadius * cos(currentAngle)).toFloat(),
                    (centerY + innerRadius * sin(currentAngle)).toFloat()
                )
            )
            currentAngle += forwardAngleMove
            starPtsList.add(
                PointF(
                    (centerX + outerRadius * cos(currentAngle)).toFloat(),
                    (centerY + outerRadius * sin(currentAngle)).toFloat()
                )
            )
        }
    }
}