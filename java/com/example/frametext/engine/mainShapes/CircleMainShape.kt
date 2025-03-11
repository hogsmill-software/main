package com.example.frametext.engine.mainShapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import com.example.frametext.engine.mainSizes.SquareMainSizes
import com.example.frametext.shapes.edge.ColoredEdgeShapeDetails
import com.example.frametext.shapes.edge.EdgeShapeDetails
import com.example.frametext.shapes.edge.EmojiEdgeShapeDetails
import com.example.frametext.shapes.edge.SymbolEdgeShapeDetails
import kotlin.math.asin
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

class CircleMainShape(
    private val canvas: Canvas,
    private var mainSizes: SquareMainSizes,
    private val closestDistance: Int,
    private var sd: EdgeShapeDetails
) :
    MainShape {
    override fun draw() {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val tf = Typeface.create("TimesRoman", Typeface.NORMAL)
        paint.typeface = tf
        paint.textSize = 150f
        var horizontalAdjustment = 0f
        var verticalAdjustment = 0f
        when (sd) {
            is ColoredEdgeShapeDetails -> {
                paint.color = (sd as ColoredEdgeShapeDetails).color
            }
            is EmojiEdgeShapeDetails -> {
                horizontalAdjustment = (sd as EmojiEdgeShapeDetails).horizontalAdjustment - 3
                verticalAdjustment = (sd as EmojiEdgeShapeDetails).verticalAdjustment
            }
            is SymbolEdgeShapeDetails -> {
                paint.color = (sd as SymbolEdgeShapeDetails).color
                horizontalAdjustment = sd.width / 2.0f - sd.centerX
            }
        }
        val ptCentreX: Float = mainSizes.width / 2.0f
        val ptCentreY: Float = mainSizes.height / 2.0f
        val startAngle = 0.0 // Math.PI/2.0;

        // There should be very little difference between these but still visible in case width and height of shape
        val horizontalRadius: Float =
            mainSizes.width / 2.0f - mainSizes.margin - sd.width / 2.0f
        val verticalRadius: Float =
            mainSizes.height/ 2.0f - mainSizes.margin - sd.height / 2.0f
        val alpha = 2 * asin(closestDistance / (2.0 * horizontalRadius))
        val numShapesPerCircle = ceil(2 * Math.PI / alpha).toInt()
        val beta = 2 * Math.PI / numShapesPerCircle
        for (i in 0 until numShapesPerCircle) {
            val xPt: Float = ptCentreX + horizontalRadius * cos(startAngle + i * beta)
                .toFloat() - sd.width / 2.0f + horizontalAdjustment
            val yPt: Float = ptCentreY + verticalRadius * sin(startAngle + i * beta)
                .toFloat() + sd.bottomAdjustment + sd.height / 2.0f + verticalAdjustment
            sd.draw(canvas, xPt, yPt, paint)
        }
    }
}
