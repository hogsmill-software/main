package com.example.frametext.engine.textBoundaries

import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import com.example.frametext.engine.TextFormattingDetails
import com.example.frametext.engine.TextRectDetails
import com.example.frametext.engine.mainSizes.SquareMainSizes
import com.example.frametext.shapes.edge.EdgeShapeDetails
import com.example.frametext.shapes.edge.EmojiEdgeShapeDetails
import kotlin.math.*

class DiamondTextBoundaries(
    paint: Paint,
    private val mainSizes: SquareMainSizes,
    private val sd: EdgeShapeDetails,
    private val tfd: TextFormattingDetails
) :
    TextBoundaries {
    private val rectLst: MutableList<TextRectDetails> = ArrayList()
    private val textAscent: Float = paint.ascent()
    private val textDescent: Float = paint.descent()

    private fun getXLineCoordinateFromY(y: Float, ptA: PointF, ptB: PointF): Float {
        // Gets the x coordinate of line joining ptA and ptB
        // equation of line through 2 points, see (for example) https://www.bbc.co.uk/bitesize/guides/zqfrw6f/revision/5
        // x-slope is + or - slope given by:
        val xSlope = (ptB.x - ptA.x)/(ptB.y - ptA.y)
        // x = xSlope*y + c and c = (ptA.x*ptB.y - ptB.x*ptA.y)/(ptB.y - ptA.y) (worked this out on paper)
        return xSlope*y + (ptA.x*ptB.y - ptB.x*ptA.y)/(ptB.y - ptA.y)
    }

    private fun getXDiamondIntersectionsFromY(y: Float, ptLeftCorner: PointF, ptTopCorner: PointF,
                                                 ptRightCorner: PointF, ptBottomCorner: PointF): IntArray {
        val retVal = IntArray(2)
        if (y > ptLeftCorner.y) {
            retVal[0] = getXLineCoordinateFromY(y, ptLeftCorner, ptTopCorner).toInt()
            retVal[1] = getXLineCoordinateFromY(y, ptTopCorner, ptRightCorner).toInt()
        }
        else {
            retVal[0] = getXLineCoordinateFromY(y, ptLeftCorner, ptBottomCorner).toInt()
            retVal[1] = getXLineCoordinateFromY(y, ptBottomCorner, ptRightCorner).toInt()
        }

        return retVal
    }

    override fun computeTextRectangles(): List<TextRectDetails> {
        var verticalAdjustment = 0f
        if (sd is EmojiEdgeShapeDetails) {
            verticalAdjustment = sd.verticalAdjustment
        }
        val rect = RectF(
            mainSizes.margin + sd.width,
            mainSizes.margin + sd.height + verticalAdjustment,
            mainSizes.width - mainSizes.margin - sd.width,
            mainSizes.height - mainSizes.margin - sd.height
        )
        val distTxtShape = sqrt(2f)*tfd.txtHeartsMargin
        //innerRect delimitates the area for drawing text.
        val innerRect = RectF(
            rect.left + distTxtShape,
            rect.top + distTxtShape,
            rect.right - distTxtShape,
            rect.bottom - distTxtShape
        )
        // Middle of sides are:
        val verticalMiddle = (innerRect.top + innerRect.bottom)/2f
        val horizontalMiddle = (innerRect.left + innerRect.right)/2f
        // Coordinates of 4 corners of diamond:
        val ptLeftCorner = PointF(innerRect.left, verticalMiddle)
        val ptTopCorner = PointF(horizontalMiddle, innerRect.bottom)
        val ptBottomCorner = PointF(horizontalMiddle, innerRect.top)
        val ptRightCorner = PointF(innerRect.right, verticalMiddle)

        val innerTextTopLine = innerRect.top - textAscent // Top text line position
        val innerTextBottomLine = innerRect.bottom - textDescent // Bottom of last line position

        var numLines =
            floor((innerTextBottomLine - innerTextTopLine).toDouble() / tfd.lineHeight).toInt()
        if (numLines <= 0) {
            numLines = 1
        }
        val lineHeight = (innerTextBottomLine - innerTextTopLine) / numLines
        for (lineIdx in 0 until numLines) {
            val yPos = innerTextTopLine + lineIdx * lineHeight

            val xTopPts = getXDiamondIntersectionsFromY(yPos, ptLeftCorner, ptTopCorner, ptRightCorner, ptBottomCorner)
            val xBottomPts = getXDiamondIntersectionsFromY(yPos - textAscent, ptLeftCorner, ptTopCorner, ptRightCorner, ptBottomCorner)

            val rc = Rect(
                max(xTopPts[0], xBottomPts[0]),
                (yPos - (textAscent + textDescent)).toInt(),
                min(
                    xTopPts[1], xBottomPts[1]
                ),
                (yPos - textAscent).toInt()
            )
            rectLst.add(TextRectDetails(rc))
        }
        return rectLst
    }
}