package com.example.frametext.engine.mainShapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Typeface
import com.example.frametext.engine.mainSizes.HeartMainSizes
import com.example.frametext.shapes.edge.ColoredEdgeShapeDetails
import com.example.frametext.shapes.edge.EdgeShapeDetails
import com.example.frametext.shapes.edge.SymbolEdgeShapeDetails
import kotlin.math.*

class HeartMainShape(
    private val canvas: Canvas,
    mainSizes: HeartMainSizes,
    closestDistance: Int,
    sd: EdgeShapeDetails
) :
    MainShape {
    private var mainSizes: HeartMainSizes
    private val closestDistance: Int
    private var sd: EdgeShapeDetails
    private val bottomAdjust: Float
    private fun computeSideHearts(
        totalHeartCount: Int,
        distance: Double,
        startAngle: Double,
        beta: Double,
        heartsLst: MutableList<Point>,
        ptCircleCentre: Point,
        left: Boolean
    ) {
        val gamma = 2 * asin(distance / (2 * mainSizes.radius)) // seems correct
        var heartCount = 0
        var angle = if (left) startAngle else -startAngle + Math.PI
        if (left) {
            while (angle < beta) {
                heartsLst.add(
                    Point(
                        (ptCircleCentre.x + mainSizes.radius * cos(angle) - sd.centerX).toInt(),
                        (ptCircleCentre.y - mainSizes.radius * sin(angle) - sd.centerY).toInt()
                    )
                )
                angle += gamma
                heartCount++
            }
        } else {
            while (angle > -beta + Math.PI) {
                heartsLst.add(
                    Point(
                        (ptCircleCentre.x + mainSizes.radius * cos(angle) - sd.centerX).toInt(),
                        (ptCircleCentre.y - mainSizes.radius * sin(angle) - sd.centerY).toInt()
                    )
                )
                angle -= gamma
                heartCount++
            }
        }
        val a: Double =
            ptCircleCentre.x + mainSizes.radius * cos(if (left) beta else Math.PI - beta) - sd.centerX
        val b: Double =
            ptCircleCentre.y - mainSizes.radius * sin(if (left) beta else Math.PI - beta) - sd.centerY
        val c: Double = mainSizes.width / 2.0 - sd.centerX
        val d: Double =
            (mainSizes.height - mainSizes.margin + bottomAdjust).toDouble() //hd.getHeartCenterY();
        val ptLast = heartsLst[heartsLst.size - 1]

        // equation of a line through (a, b) and (c, d) is:
        // x + aa*y + bb = 0 where:
        // aa = (c - a)/(b - d) and bb = (ad - bc)/(b - d)
        val aa = (c - a) / (b - d)
        val bb = (a * d - b * c) / (b - d)
        // see link for calculations below
        // https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
        val distanceFromLine = abs(ptLast.x + aa * ptLast.y + bb) / sqrt(1 + aa * aa)
        val junctionX = (aa * (aa * ptLast.x - ptLast.y) - bb) / (1 + aa * aa)
        val junctionY = (-aa * ptLast.x + ptLast.y - aa * bb) / (1 + aa * aa)
        val shortDist = sqrt(distance * distance - distanceFromLine * distanceFromLine)
        val zeta: Double = atan(-1 / aa) + (if (left) 0 else Math.PI).toDouble()
        var lineX = junctionX + shortDist * cos(zeta)
        var lineY = junctionY + shortDist * sin(zeta)
        heartsLst.add(Point(lineX.toInt(), lineY.toInt()))
        while (heartCount <= totalHeartCount) {
            lineX += distance * cos(zeta)
            lineY += distance * sin(zeta)
            heartsLst.add(Point(lineX.toInt(), lineY.toInt()))
            heartCount++
        }
    }

    override fun draw() {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val tf = Typeface.create("TimesRoman", Typeface.NORMAL)
        paint.typeface = tf
        paint.textSize = 150f
        if (sd is ColoredEdgeShapeDetails) {
            paint.color = (sd as ColoredEdgeShapeDetails).color
        } else if (sd is SymbolEdgeShapeDetails) {
            paint.color = (sd as SymbolEdgeShapeDetails).color
        }
        val heartsLst: MutableList<Point> = ArrayList()

        // left circle
        val ptLeftTopCircleCentre: Point = if (sd is SymbolEdgeShapeDetails) {
            Point(
                (mainSizes.margin + mainSizes.radius + sd.width / 2.0f).toInt(),
                (mainSizes.margin + mainSizes.radius + sd.height / 2.0f).toInt()
            )
        } else {
            Point(
                (mainSizes.margin + mainSizes.radius + sd.centerX).toInt(),
                (mainSizes.margin + mainSizes.radius - sd.centerY).toInt()
            )
        }
        val startAngle =
            acos((mainSizes.width / 2.0 - ptLeftTopCircleCentre.x) / mainSizes.radius)
        val vertDistBottomPt: Double =
            (mainSizes.height - 2 * mainSizes.margin - mainSizes.radius).toDouble() // y-coordinate top circle centres - y coordinate of bottom of heart
        val alpha = atan(vertDistBottomPt / mainSizes.radius)
        val phi = atan(vertDistBottomPt / (mainSizes.radius * cos(startAngle)))
        val beta = 2 * Math.PI - alpha - phi
        val circleDist: Double = (beta - startAngle) * mainSizes.radius
        val totDistance = circleDist + vertDistBottomPt

        // first approximation of distance using linear length
        var totalHeartCount = 1
        var distance: Double
        var oldDistance: Double
        do {
            distance = totDistance / ++totalHeartCount
        } while (distance > closestDistance)
        distance = totDistance / --totalHeartCount

        // distance is approximate - between last heart on curve and first on line, we are assuming distance as section of curve
        // and line when it should be just a line.

        // following approximations using numerical computation of line...
        // prevError is there to prevent hanging in scenario where error margin doesn't reduce
        var error = 100000000.0
        var prevError: Double
        do {
            computeSideHearts(
                totalHeartCount,
                distance,
                startAngle,
                beta,
                heartsLst,
                ptLeftTopCircleCentre,
                true
            )
            val ptLast = heartsLst[heartsLst.size - 1]
            prevError = error
            oldDistance = distance
            error = sqrt(
                (mainSizes.width / 2.0 - sd.centerX - ptLast.x).pow(2.0) + (mainSizes.height - mainSizes.margin + bottomAdjust - ptLast.y).toDouble()
                    .pow(2.0)
            )
            if (error > 1 && error < prevError) {
                distance += if (mainSizes.height - mainSizes.margin + bottomAdjust > ptLast.y) error / totalHeartCount else -error / totalHeartCount
                heartsLst.clear()
            }
        } while (error > 1 && error < prevError)
        if (error > prevError) {
            heartsLst.clear()
            computeSideHearts(
                totalHeartCount,
                oldDistance,
                startAngle,
                beta,
                heartsLst,
                ptLeftTopCircleCentre,
                true
            )
        }

        // right circle
        val ptRightTopCircleCentre: Point = if (sd is SymbolEdgeShapeDetails) {
            Point(
                (mainSizes.width - mainSizes.margin - mainSizes.radius - sd.width / 2.0f).toInt(),
                (mainSizes.margin + mainSizes.radius + sd.height / 2.0f).toInt()
            )
        } else {
            Point(
                (mainSizes.width - mainSizes.margin - mainSizes.radius - sd.centerX).toInt(),
                (mainSizes.margin + mainSizes.radius - sd.centerY).toInt()
            )
        }
        computeSideHearts(
            totalHeartCount,
            distance,
            startAngle,
            beta,
            heartsLst,
            ptRightTopCircleCentre,
            false
        )
        for (pt2d in heartsLst) {
            sd.draw(canvas, pt2d.x.toFloat(), pt2d.y.toFloat(), paint)
        }
    }

    init {
        this.mainSizes = mainSizes
        this.closestDistance = closestDistance
        this.sd = sd
        bottomAdjust = sd.bottomAdjustment
    }
}