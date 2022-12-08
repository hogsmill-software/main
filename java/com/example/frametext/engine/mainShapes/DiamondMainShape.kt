package com.example.frametext.engine.mainShapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import com.example.frametext.engine.mainSizes.SquareMainSizes
import com.example.frametext.shapes.edge.ColoredEdgeShapeDetails
import com.example.frametext.shapes.edge.EdgeShapeDetails
import com.example.frametext.shapes.edge.EmojiEdgeShapeDetails
import com.example.frametext.shapes.edge.SymbolEdgeShapeDetails
import kotlin.math.*

class DiamondMainShape(
    private val canvas: Canvas,
    mainSizes: SquareMainSizes,
    closestDistance: Int,
    sd: EdgeShapeDetails
) :
    MainShape {
    private var mainSizes: SquareMainSizes
    private val closestDistance: Int
    private var sd: EdgeShapeDetails
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

        val topYCoordinate: Float = mainSizes.height + sd.bottomAdjustment - mainSizes.margin + verticalAdjustment
        val bottomYCoordinate: Float = sd.bottomAdjustment + sd.height + mainSizes.margin + verticalAdjustment
        val leftXCoordinate: Float = mainSizes.margin + horizontalAdjustment
        val rightXCoordinate: Float = mainSizes.width - mainSizes.margin - sd.width + horizontalAdjustment

        // Coordinates of 4 corners of diamond:
        val leftCorner = PointF(leftXCoordinate, (topYCoordinate + bottomYCoordinate)/2f)
        val topCorner = PointF((leftXCoordinate + rightXCoordinate)/2f, topYCoordinate)
        // val bottomCorner = PointF((leftXCoordinate + rightXCoordinate)/2f, bottomYCoordinate) // Strange but never needed
        val rightCorner = PointF(rightXCoordinate, (topYCoordinate + bottomYCoordinate)/2f)

        // Length of a side of diamond given by:
        val lengthSide = sqrt((topCorner.x - leftCorner.x).pow(2) + (topCorner.y - leftCorner.y).pow(2))
        val shapeCount: Int = (lengthSide / closestDistance).toInt()

        // equation of line through 2 points, see (for example) https://www.bbc.co.uk/bitesize/guides/zqfrw6f/revision/5
        // slope is + or - slope given by:
        val slope = (topCorner.y - leftCorner.y)/(topCorner.x - leftCorner.x)

        // y = slope*x + ?? so ?? = y - slope*x
        // ...y.x.Constants are the c in equation y = slope * x + c
        val topLeftConstant = leftCorner.y - slope*leftCorner.x
        val bottomLeftConstant = leftCorner.y + slope*leftCorner.x
        val topRightConstant = rightCorner.y + slope*rightCorner.x
        val bottomRightConstant = rightCorner.y - slope*rightCorner.x

        for (idx in 0 until shapeCount) {
            val leftX = leftCorner.x + idx*(topCorner.x - leftCorner.x)/shapeCount
            val rightX = topCorner.x + idx*(rightCorner.x - topCorner.x)/shapeCount
            sd.draw(canvas, leftX, slope*leftX + topLeftConstant, paint)
            sd.draw(canvas, leftX, -slope*leftX + bottomLeftConstant, paint)
            sd.draw(canvas, rightX, -slope*rightX + topRightConstant, paint)
            sd.draw(canvas, rightX, slope*rightX + bottomRightConstant, paint)
        }

        // Draw last rightmost corner, otherwise it is missing.
        sd.draw(canvas, rightCorner.x, rightCorner.y, paint)
    }

    init {
        this.mainSizes = mainSizes
        this.closestDistance = closestDistance
        this.sd = sd
    }
}