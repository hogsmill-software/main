package com.example.frametext.engine.mainShapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import com.example.frametext.engine.mainSizes.SquareMainSizes
import com.example.frametext.shapes.edge.ColoredEdgeShapeDetails
import com.example.frametext.shapes.edge.EdgeShapeDetails
import com.example.frametext.shapes.edge.EmojiEdgeShapeDetails
import com.example.frametext.shapes.edge.SymbolEdgeShapeDetails

class SquareMainShape(
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
                horizontalAdjustment = (sd as EmojiEdgeShapeDetails).horizontalAdjustment
                verticalAdjustment = (sd as EmojiEdgeShapeDetails).verticalAdjustment
            }
            is SymbolEdgeShapeDetails -> {
                paint.color = (sd as SymbolEdgeShapeDetails).color
                horizontalAdjustment = sd.width / 2.0f - sd.centerX
            }
        }
        // Note: the vertical distance between shapes is different from the horizontal distance between shapes.
        val horizontalLen = (mainSizes.width - 2 * mainSizes.margin - sd.width).toInt()
        val verticalLen = (mainSizes.width - 2 * mainSizes.margin - sd.height).toInt()
        val topYCoordinate: Float =
            mainSizes.height + sd.bottomAdjustment - mainSizes.margin + verticalAdjustment
        val bottomYCoordinate: Float =
            sd.bottomAdjustment + sd.height + mainSizes.margin + verticalAdjustment
        val leftXCoordinate: Float = mainSizes.margin + horizontalAdjustment
        val rightXCoordinate: Float =
            mainSizes.width - mainSizes.margin - sd.width + horizontalAdjustment
        val horizontalShapeSideCount = horizontalLen / closestDistance
        val horizontalDistShape = horizontalLen.toFloat() / horizontalShapeSideCount
        val verticalShapeSideCount = verticalLen / closestDistance
        val verticalDistShape = verticalLen.toFloat() / verticalShapeSideCount
        var xPlotPt: Float
       var shapePos = 0
        // Draws top and bottom side
        do {
            xPlotPt = leftXCoordinate + shapePos * horizontalDistShape
            sd.draw(canvas, xPlotPt, topYCoordinate, paint)
            sd.draw(canvas, xPlotPt, bottomYCoordinate, paint)
        } while (++shapePos <= horizontalShapeSideCount)
        // Draw left and right side
        shapePos = 1
        var yPlotPt: Float
        do {
            yPlotPt = bottomYCoordinate + shapePos * verticalDistShape
            sd.draw(canvas, leftXCoordinate, yPlotPt, paint)
            sd.draw(canvas, rightXCoordinate, yPlotPt, paint)
        } while (++shapePos < verticalShapeSideCount)
    }
}
