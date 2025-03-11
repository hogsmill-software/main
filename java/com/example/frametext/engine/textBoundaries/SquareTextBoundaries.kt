package com.example.frametext.engine.textBoundaries

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.example.frametext.engine.TextFormattingDetails
import com.example.frametext.engine.TextRectDetails
import com.example.frametext.engine.mainSizes.SquareMainSizes
import com.example.frametext.shapes.edge.EdgeShapeDetails
import com.example.frametext.shapes.edge.EmojiEdgeShapeDetails
import kotlin.math.floor

class SquareTextBoundaries(
    paint: Paint,
    private val mainSizes: SquareMainSizes,
    private val sd: EdgeShapeDetails,
    private val tfd: TextFormattingDetails
) :
    TextBoundaries {
    private val rectLst: MutableList<TextRectDetails> = ArrayList()
    private val textAscent: Float = paint.ascent()
    private val textDescent: Float = paint.descent()
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
        //innerRect delimitates the area for drawing text.
        val innerRect = RectF(
            rect.left + tfd.txtHeartsMargin,
            rect.top + tfd.txtHeartsMargin,
            rect.right - tfd.txtHeartsMargin,
            rect.bottom - tfd.txtHeartsMargin
        )
        val innerTextTopLine = innerRect.top - textAscent // Top text line position
        val innerTextBottomLine = innerRect.bottom - textDescent // Bottom of last line position
        val numLines =
            floor((innerTextBottomLine - innerTextTopLine).toDouble() / tfd.lineHeight).toInt()
        val lineHeight = (innerTextBottomLine - innerTextTopLine) / numLines
        for (rectIdx in 0..numLines) {
            // The top of rectangle is y-coordinate of where text is written. Bottom is lowest point of text
            val rc = Rect(
                innerRect.left.toInt(),
                (innerTextTopLine + rectIdx * lineHeight).toInt(),
                innerRect.right.toInt(),
                (innerTextTopLine + rectIdx * lineHeight + textDescent).toInt()
            )
            rectLst.add(TextRectDetails(rc))
        }
        return rectLst
    }
}
