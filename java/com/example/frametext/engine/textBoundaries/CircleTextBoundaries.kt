package com.example.frametext.engine.textBoundaries

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.example.frametext.engine.TextFormattingDetails
import com.example.frametext.engine.TextRectDetails
import com.example.frametext.engine.mainSizes.SquareMainSizes
import com.example.frametext.shapes.edge.EdgeShapeDetails
import kotlin.math.*

class CircleTextBoundaries(
    paint: Paint,
    private val mainSizes: SquareMainSizes,
    private val sd: EdgeShapeDetails,
    private val tfd: TextFormattingDetails
) :
    TextBoundaries {
    private val rectLst: MutableList<TextRectDetails> = ArrayList()
    private val textAscent: Float = paint.ascent()
    private val textDescent: Float = paint.descent()
    private fun getXEllipseIntersectionsFromY(y: Float): IntArray {
        // equation of an ellipse is (x - Cx)**2/a**2 + (y - Cy)**2/b**2 = 1
        // so x = Cx +- a*sqrt(1 - (y - Cy)**2/b**2)
        // X and Y Centre of circle
        val cx: Double = mainSizes.width / 2.0
        val cy: Double = mainSizes.height / 2.0
        // Horizontal and vertical radius
        val a: Double =
            mainSizes.width / 2.0 - mainSizes.margin - sd.width - tfd.txtHeartsMargin
        var b: Double =
            mainSizes.height / 2.0 - mainSizes.margin - sd.height - tfd.txtHeartsMargin
        if (b == 0.0) {
            b = 1.0
        }
        var sqrtPart = 1 - (y - cy).pow(2.0) / (b * b)
        if (sqrtPart < 0) {
            sqrtPart = 0.0
        }
        val otherSide = a * sqrt(sqrtPart)
        val retVal = IntArray(2)
        retVal[0] = (cx - otherSide).toInt()
        retVal[1] = (cx + otherSide).toInt()
        return retVal
    }

    override fun computeTextRectangles(): List<TextRectDetails> {
        val rect = RectF(
            mainSizes.margin + sd.width,
            mainSizes.margin + sd.height,
            mainSizes.width - mainSizes.margin - sd.width,
            mainSizes.height - mainSizes.margin - sd.height
        )
        // InnerRect delimitates the area for drawing text.
        val innerRect = RectF(
            rect.left + tfd.txtHeartsMargin,
            rect.top + tfd.txtHeartsMargin,
            rect.right - tfd.txtHeartsMargin,
            rect.bottom - tfd.txtHeartsMargin
        )
        val topBottomMargin = -50f
        val innerTextTopLine = innerRect.top - topBottomMargin
        val innerTextBottomLine = innerRect.bottom + textDescent + topBottomMargin
        var numLines =
            floor((innerTextBottomLine - innerTextTopLine).toDouble() / tfd.lineHeight).toInt()
        if (numLines <= 0) {
            numLines = 1
        }
        val lineHeight = (innerTextBottomLine - innerTextTopLine) / numLines
        for (lineIdx in 0 until numLines) {
            val yPos = innerTextTopLine + lineIdx * lineHeight
            val xTopPts = getXEllipseIntersectionsFromY(yPos)
            val xBottomPts = getXEllipseIntersectionsFromY(yPos - textAscent)
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
