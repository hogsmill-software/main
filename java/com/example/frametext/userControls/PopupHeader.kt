package com.example.frametext.userControls

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import java.util.ArrayList

// This class draws a popup header.
// As in some languages, the header may not fit on a single line, this class allows multiple line display
class PopupHeader(private val header: String, private val maxWidth: Float) {
    private var headerBoundaryLst: MutableList<Pair<String, Rect>> =  ArrayList()
    var rcHeaderBounds = Rect()
        private set

    fun computeData(paint: Paint) {
        computeData(paint, header)
    }

    private fun computeData(paint: Paint, subHeader: String) {
        val rcTextBounds = Rect()
        paint.getTextBounds(subHeader, 0, subHeader.length, rcTextBounds)

        if (rcTextBounds.width() <= maxWidth) {
            headerBoundaryLst.add(0, Pair(subHeader, rcTextBounds))
            updateHeaderBounds(rcTextBounds)
        }
        else {
            for (i in subHeader.indices) {
                if (subHeader[i] == ' ' && i < subHeader.length - 1) {
                    paint.getTextBounds(subHeader, i + 1, subHeader.length, rcTextBounds)

                    if (rcTextBounds.width() <= maxWidth) {
                        val subSubHeader = subHeader.subSequence(i + 1, subHeader.length)
                        headerBoundaryLst.add(0, Pair(subSubHeader.toString(), rcTextBounds))
                        updateHeaderBounds(rcTextBounds)
                        computeData(paint, subHeader.subSequence(0, i).toString())
                        break
                    }
                }
            }
        }
    }

    private fun updateHeaderBounds(rcTextBounds: Rect) {
        if (rcHeaderBounds.width() < rcTextBounds.width()) {
            rcHeaderBounds.right = rcTextBounds.right
        }
        rcHeaderBounds.bottom += rcTextBounds.bottom -  rcTextBounds.top
    }

    fun draw(canvas: Canvas, paint: Paint, fullScreenWidth: Float, topPoint: Float) {
        var lineTopPoint = topPoint
        for (idx in 0 until headerBoundaryLst.size) {
            lineTopPoint += headerBoundaryLst[idx].second.height()
            canvas.drawText(
                headerBoundaryLst[idx].first,
                (fullScreenWidth - headerBoundaryLst[idx].second.width()) / 2.0f,
                lineTopPoint,
                paint
            )
        }
    }
}