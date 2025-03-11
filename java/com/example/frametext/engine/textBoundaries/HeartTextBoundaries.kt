package com.example.frametext.engine.textBoundaries

import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import com.example.frametext.engine.TextFormattingDetails
import com.example.frametext.engine.TextRectDetails
import com.example.frametext.engine.mainSizes.HeartMainSizes
import com.example.frametext.shapes.edge.EdgeShapeDetails
import com.example.frametext.shapes.edge.EmojiEdgeShapeDetails
import com.example.frametext.shapes.edge.SymbolEdgeShapeDetails
import kotlin.math.*

class HeartTextBoundaries(
    paint: Paint,
    private val mainSizes: HeartMainSizes,
    private var sd: EdgeShapeDetails,
    private val tfd: TextFormattingDetails
) :
    TextBoundaries {
    private val rectLst: MutableList<TextRectDetails> = ArrayList()
    private val textAscent: Float = paint.ascent()
    private fun getXCircleIntersectionsFromY(y: Int, left: Boolean): IntArray {
        // centre of left circle is x_centre = margin + 2*heartCenterX + (radius - heartCenterX) = margin + heartCenterX + radius
        // 					        y_centre = margin - 2*heartCenterY + (radius + heartCenterY) = margin - heartCenterY + radius
        // centre of right circle is x_centre = width - margin - 2 * radius + radius - heartCenterX = width - margin - radius - heartCenterX
        // 					   y_centre = margin - 2*heartCenterY + (radius + heartCenterY) = margin - heartCenterY + radius
        // equation of top left inner circle is:
        // (margin + heartCenterX + radius) + (radius - heartCenterX)*cos(angle)
        // (margin - heartCenterY + radius) + (radius + heartCenterY)*sin(angle) where angle = 0 to 2*pi.
        // equation of top right inner circle is:
        // (width - margin - radius - heartCenterX) + (radius - heartCenterX)*cos(angle)
        //  (margin - heartCenterY + radius) + (radius + heartCenterY)*sin(angle) where angle = 0 to 2*pi.
        // these are not circles but ovals!
        // adjustedHeartCenter is an arbitrary quickfix that gives better results for emoji
        val adjustedHeartCenter: Float = sd.centerX - 10
        val angle =
            asin((y - (mainSizes.margin - sd.centerY + mainSizes.radius).toDouble()) / (mainSizes.radius - tfd.txtHeartsMargin + sd.centerY))
        val xCentre: Double =
            if (left) (mainSizes.margin + adjustedHeartCenter /*hd.getHeartCenterX()*/ + mainSizes.radius).toDouble() else (mainSizes.width - mainSizes.margin - mainSizes.radius - adjustedHeartCenter).toDouble() /*hd.getHeartCenterX()*/
        val xFirst =
            (xCentre + (mainSizes.radius - tfd.txtHeartsMargin - sd.centerX) * cos(
                angle
            )).toInt()
        val xSecond =
            (xCentre + (mainSizes.radius - tfd.txtHeartsMargin - sd.centerX) * cos(
                angle + Math.PI
            )).toInt()
        val retVal = IntArray(2)
        retVal[0] = min(xFirst, xSecond)
        retVal[1] = max(xFirst, xSecond)
        return retVal
    }

    private fun getXEllipseIntersectionsFromYSymbol(y: Int, left: Boolean): IntArray {
        // This version is for symbols - will probably replace getXCircleIntersectionsFromY. Has no quick fix arbitrary correction,
        // so should be able to increase/decrease symbol size relative to text.
        // This version has correct circle centres.
        // width and height refer to width and height of symbol.
        // centre of left circle is x_centre = margin + radius + width/2.0
        // 					        y_centre = margin + radius + height/2.0
        // centre of right circle is x_centre = width - margin - radius - width/2.0
        // 					         y_centre = margin + radius + height/2.0
        // equation of an ellipse is (x - Cx)**2/a**2 + (y - Cy)**2/b**2 = 1
        // so x = Cx +- a*sqrt(1 - (y - Cy)**2/b**2)
        val cx: Double =
            if (left) mainSizes.margin + mainSizes.radius + sd.width / 2.0 else mainSizes.width - mainSizes.margin - mainSizes.radius - sd.width / 2.0
        val cy: Double = mainSizes.margin + mainSizes.radius + sd.height / 2.0
        val a: Double = mainSizes.radius - sd.width / 2.0 - tfd.txtHeartsMargin
        val b: Double = mainSizes.radius - sd.height / 2.0 - tfd.txtHeartsMargin
        val retVal = IntArray(2)
        val otherSide = a * sqrt(1 - (y - cy).pow(2.0) / (b * b))
        retVal[0] = (cx - otherSide).toInt()
        retVal[1] = (cx + otherSide).toInt()
        return retVal
    }

    private fun getXEllipseIntersectionsFromY(y: Int, left: Boolean): IntArray {
        // centre of left circle is x_centre = margin + 2*heartCenterX + (radius - heartCenterX) = margin + heartCenterX + radius
        // 					        y_centre = margin - 2*heartCenterY + (radius + heartCenterY) = margin - heartCenterY + radius
        // centre of right circle is x_centre = width - margin - 2 * radius + radius - heartCenterX = width - margin - radius - heartCenterX
        // 					   y_centre = margin - 2*heartCenterY + (radius + heartCenterY) = margin - heartCenterY + radius
        // equation of an ellipse is (x - Cx)**2/a**2 + (y - Cy)**2/b**2 = 1
        // so x = Cx +- a*sqrt(1 - (y - Cy)**2/b**2)
        val cx: Double =
            if (left) (mainSizes.margin + mainSizes.radius + sd.centerX).toDouble() else (mainSizes.width - mainSizes.margin - mainSizes.radius - sd.centerX).toDouble()
        val cy: Double = (mainSizes.margin + mainSizes.radius + sd.centerY).toDouble()
        val a: Double = (mainSizes.radius - sd.centerX - tfd.txtHeartsMargin).toDouble()
        val b: Double = (mainSizes.radius - sd.centerY - tfd.txtHeartsMargin).toDouble()
        val retVal = IntArray(2)
        val otherSide = a * sqrt(1 - (y - cy).pow(2.0) / (b * b))
        retVal[0] = (cx - otherSide).toInt()
        retVal[1] = (cx + otherSide).toInt()
        return retVal
    }

    private fun computeBottomTrianglePts(): Array<IntArray> {
        val ptLeftTopCircleCentre = Point(
            (mainSizes.margin + mainSizes.radius + sd.centerX).toInt(),
            (mainSizes.margin + mainSizes.radius - sd.centerY).toInt()
        )
        val ptRightTopCircleCentre = Point(
            (mainSizes.width - mainSizes.margin - mainSizes.radius - sd.centerX).toInt(),
            (mainSizes.margin + mainSizes.radius - sd.centerY).toInt()
        )
        val startAngle =
            acos((mainSizes.width / 2.0 - ptLeftTopCircleCentre.x) / mainSizes.radius)
        val vertDistBottomPt: Double =
            (mainSizes.height - 2 * mainSizes.margin - mainSizes.radius).toDouble() // y-coordinate top circle centres - y coordinate of bottom of heart
        val alpha = atan(vertDistBottomPt / mainSizes.radius)
        val phi = atan(vertDistBottomPt / (mainSizes.radius * cos(startAngle)))
        val beta = 2 * Math.PI - alpha - phi
        val pt1x: Double =
            ptLeftTopCircleCentre.x + mainSizes.radius * cos(beta) - tfd.txtHeartsMargin / sin(
                beta
            ) /*- heartCenterX + heartCenterWidth*/
        var pt1y: Double =
            ptLeftTopCircleCentre.y - mainSizes.radius * sin(beta) - sd.centerY /* + heightAdjustment*/
        val pt2x: Double =
            ptRightTopCircleCentre.x + mainSizes.radius * cos(Math.PI - beta) + tfd.txtHeartsMargin / sin(
                beta
            ) /*- heartCenterX + heartCenterWidth*/
        var pt2y: Double =
            ptRightTopCircleCentre.y - mainSizes.radius * sin(Math.PI - beta) - sd.centerY /*+ heightAdjustment*/
        if (sd !is EmojiEdgeShapeDetails) {
            //	if (!hd.getUseEmoji()) {
            pt1y += 2.0f * sd.height
            pt2y += 2.0f * sd.height
            // I've already added for pt3.y elsewhere - tidy when have time. This works safely, but messy.
        }
        val pt3x: Double = mainSizes.width / 2.0
        var pt3y: Double =
            mainSizes.height - mainSizes.margin + tfd.txtHeartsMargin / cos(
                Math.PI / 2.0 - beta
            )
        val zeta = atan((pt1y - pt3y) / (pt1x - pt3x))
        val heightAdjustment: Double = -0.5 * sd.width * sin(zeta) - sd.height
        pt1y += heightAdjustment
        pt2y += heightAdjustment
        pt3y += heightAdjustment
        val xPoints = intArrayOf(pt1x.toInt(), pt3x.toInt(), pt2x.toInt())
        val yPoints = intArrayOf(pt1y.toInt(), pt3y.toInt(), pt2y.toInt())
        return arrayOf(xPoints, yPoints)
    }

    private fun computeBottomTrianglePtsSymbols(): Array<IntArray> {
        val ptLeftTopCircleCentre = Point(
            (mainSizes.margin + mainSizes.radius + sd.width / 2.0).toInt(),
            (mainSizes.margin + mainSizes.radius + sd.width / 2.0).toInt()
        )
        val ptRightTopCircleCentre = Point(
            (mainSizes.width - mainSizes.margin - mainSizes.radius - sd.width / 2.0).toInt(),
            (mainSizes.margin + mainSizes.radius + sd.width / 2.0).toInt()
        )
        val startAngle =
            acos((mainSizes.width / 2.0 - ptLeftTopCircleCentre.x) / mainSizes.radius)
        val vertDistBottomPt: Double =
            (mainSizes.height - 2 * mainSizes.margin - mainSizes.radius).toDouble() // y-coordinate top circle centres - y coordinate of bottom of heart
        val alpha = atan(vertDistBottomPt / mainSizes.radius)
        val phi = atan(vertDistBottomPt / (mainSizes.radius * cos(startAngle)))
        val beta = 2 * Math.PI - alpha - phi
        val pt1x: Double =
            ptLeftTopCircleCentre.x + mainSizes.radius * cos(beta) - tfd.txtHeartsMargin / sin(
                beta
            ) /*- heartCenterX + heartCenterWidth*/
        var pt1y: Double =
            ptLeftTopCircleCentre.y - mainSizes.radius * sin(beta) + sd.height
        val pt2x: Double =
            ptRightTopCircleCentre.x + mainSizes.radius * cos(Math.PI - beta) + tfd.txtHeartsMargin / sin(
                beta
            ) /*- heartCenterX + heartCenterWidth*/
        var pt2y: Double =
            ptRightTopCircleCentre.y - mainSizes.radius * sin(Math.PI - beta) + sd.height
        val pt3x: Double = mainSizes.width / 2.0
        var pt3y: Double =
            mainSizes.height - mainSizes.margin + tfd.txtHeartsMargin / cos(
                Math.PI / 2.0 - beta
            )
        val zeta = atan((pt1y - pt3y) / (pt1x - pt3x))
        val heightAdjustment: Double = -0.5 * sd.width * sin(zeta) - sd.height
        pt1y += heightAdjustment
        pt2y += heightAdjustment
        pt3y += heightAdjustment
        val xPoints = intArrayOf(pt1x.toInt(), pt3x.toInt(), pt2x.toInt())
        val yPoints = intArrayOf(pt1y.toInt(), pt3y.toInt(), pt2y.toInt())
        return arrayOf(xPoints, yPoints)
    }

    override fun computeTextRectangles(): List<TextRectDetails> {
        val pts: Array<IntArray> = if (sd is SymbolEdgeShapeDetails) {
            computeBottomTrianglePtsSymbols()
        } else {
            computeBottomTrianglePts()
        }

        // Uncomment line below for testing purposes
        // drawShapes(pts);
        var yTop = (tfd.topTextMargin + mainSizes.margin + 2 * sd.height).toInt()
        when (sd) {
            is EmojiEdgeShapeDetails -> {
                while (yTop < pts[1][0]) {
                    val xTopLeft = getXCircleIntersectionsFromY(yTop, true)
                    val xTopRight = getXCircleIntersectionsFromY(yTop, false)
                    val yBottom = yTop + textAscent.toInt()
                    val xBottomLeft = getXCircleIntersectionsFromY(yBottom, true)
                    val xBottomRight = getXCircleIntersectionsFromY(yBottom, false)
                    val xLeftLeftCircle = max(xTopLeft[0], xBottomLeft[0])
                    val xRightLeftCircle = min(xTopLeft[1], xBottomLeft[1])
                    val xLeftRightCircle = max(xTopRight[0], xBottomRight[0])
                    val xRightRightCircle = min(xTopRight[1], xBottomRight[1])

                    // The 2 rectangles can be very close, without intersecting and without these intersecting heart if these were joined. When this happens, funny result with hyphen added in middle of line.
                    // see INPUT_PATH_SILLY_MESSAGE with text to heart margin of 20
                    // To remedy this, let us also merge if gap is very small - say half of heart width.
                    // Hope not introducing bug here.
                    val maxGap = (sd.width / 2.0).toInt()
                    if (xRightLeftCircle + maxGap < xLeftRightCircle && yTop < mainSizes.margin - sd.centerY + mainSizes.radius && yBottom < mainSizes.margin - sd.centerY + mainSizes.radius) {
                        rectLst.add(
                            TextRectDetails(
                                Rect(
                                    xLeftLeftCircle,
                                    yTop,
                                    xRightLeftCircle,
                                    yBottom
                                )
                            )
                        )
                        rectLst.add(
                            TextRectDetails(
                                Rect(
                                    xLeftRightCircle,
                                    yTop,
                                    xRightRightCircle,
                                    yBottom
                                )
                            )
                        )
                    } else {
                        rectLst.add(
                            TextRectDetails(
                                Rect(
                                    xLeftLeftCircle,
                                    yTop,
                                    xRightRightCircle,
                                    yBottom
                                )
                            )
                        )
                    }
                    yTop += tfd.lineHeight
                }
            }
            is SymbolEdgeShapeDetails -> {
                yTop = (tfd.topTextMargin + mainSizes.margin + sd.height - textAscent).toInt()
                while (yTop < pts[1][0]) {
                    val xTopLeft = getXEllipseIntersectionsFromYSymbol(yTop, true)
                    val xTopRight = getXEllipseIntersectionsFromYSymbol(yTop, false)
                    val yBottom = yTop + textAscent.toInt()
                    val xBottomLeft = getXEllipseIntersectionsFromYSymbol(yBottom, true)
                    val xBottomRight = getXEllipseIntersectionsFromYSymbol(yBottom, false)
                    val xLeftLeftCircle = max(xTopLeft[0], xBottomLeft[0])
                    val xRightLeftCircle = min(xTopLeft[1], xBottomLeft[1])
                    val xLeftRightCircle = max(xTopRight[0], xBottomRight[0])
                    val xRightRightCircle = min(xTopRight[1], xBottomRight[1])

                    // The 2 rectangles can be very close, without intersecting and without these intersecting heart if these were joined. When this happens, funny result with hyphen added in middle of line.
                    // see INPUT_PATH_SILLY_MESSAGE with text to heart margin of 20
                    // To remedy this, let us also merge if gap is very small - say half of heart width.
                    // Hope not introducing bug here.
                    val maxGap = (sd.width / 2.0).toInt()
                    if (xRightLeftCircle + maxGap < xLeftRightCircle && yTop < mainSizes.margin - sd.centerY + mainSizes.radius && yBottom < mainSizes.margin - sd.centerY + mainSizes.radius) {
                        rectLst.add(
                            TextRectDetails(
                                Rect(
                                    xLeftLeftCircle,
                                    yTop,
                                    xRightLeftCircle,
                                    yBottom
                                )
                            )
                        )
                        rectLst.add(
                            TextRectDetails(
                                Rect(
                                    xLeftRightCircle,
                                    yTop,
                                    xRightRightCircle,
                                    yBottom
                                )
                            )
                        )
                    } else {
                        rectLst.add(
                            TextRectDetails(
                                Rect(
                                    xLeftLeftCircle,
                                    yTop,
                                    xRightRightCircle,
                                    yBottom
                                )
                            )
                        )
                    }
                    yTop += tfd.lineHeight
                }
            }
            else -> {
                yTop -= textAscent.toInt() + 110 - tfd.txtHeartsMargin
                while (yTop < pts[1][0]) {
                    val xTopLeft = getXEllipseIntersectionsFromY(yTop, true)
                    val xTopRight = getXEllipseIntersectionsFromY(yTop, false)
                    val yBottom = yTop + textAscent.toInt()
                    val xBottomLeft = getXEllipseIntersectionsFromY(yBottom, true)
                    val xBottomRight = getXEllipseIntersectionsFromY(yBottom, false)
                    val xLeftLeftCircle = max(xTopLeft[0], xBottomLeft[0])
                    val xRightLeftCircle = min(xTopLeft[1], xBottomLeft[1])
                    val xLeftRightCircle = max(xTopRight[0], xBottomRight[0])
                    val xRightRightCircle = min(xTopRight[1], xBottomRight[1])

                    // The 2 rectangles can be very close, without intersecting and without these intersecting heart if these were joined. When this happens, funny result with hyphen added in middle of line.
                    // see INPUT_PATH_SILLY_MESSAGE with text to heart margin of 20
                    // To remedy this, let us also merge if gap is very small - say half of heart width.
                    // Hope not introducing bug here.
                    val maxGap = (sd.width / 2.0).toInt()
                    if (xRightLeftCircle + maxGap < xLeftRightCircle && yTop < mainSizes.margin - sd.centerY + mainSizes.radius && yBottom < mainSizes.margin - sd.centerY + mainSizes.radius) {
                        rectLst.add(
                            TextRectDetails(
                                Rect(
                                    xLeftLeftCircle,
                                    yTop,
                                    xRightLeftCircle,
                                    yBottom
                                )
                            )
                        )
                        rectLst.add(
                            TextRectDetails(
                                Rect(
                                    xLeftRightCircle,
                                    yTop,
                                    xRightRightCircle,
                                    yBottom
                                )
                            )
                        )
                    } else {
                        rectLst.add(
                            TextRectDetails(
                                Rect(
                                    xLeftLeftCircle,
                                    yTop,
                                    xRightRightCircle,
                                    yBottom
                                )
                            )
                        )
                    }
                    yTop += tfd.lineHeight
                }
            }
        }
        while (yTop < pts[1][1]) {
            // equation of a line through (a, b) = pt[0][0], pt[1][0] or pt[0][1], pt[1][1]
            // and (c, d) = pt[0][1], pt[1][1] is:
            // x + aa*y + bb = 0 where:
            // aa = (c - a)/(b - d) and bb = (ad - bc)/(b - d)
            val aa1 = (pts[0][1] - pts[0][0]) / (pts[1][0] - pts[1][1]).toDouble()
            val bb1 =
                (pts[0][0] * pts[1][1] - pts[1][0] * pts[0][1]) / (pts[1][0] - pts[1][1]).toDouble()
            val aa2 = (pts[0][1] - pts[0][2]) / (pts[1][2] - pts[1][1]).toDouble()
            val bb2 =
                (pts[0][2] * pts[1][1] - pts[1][2] * pts[0][1]) / (pts[1][2] - pts[1][1]).toDouble()
            // so x = - bb - aa*y
            val yBottom = yTop + textAscent.toInt()
            val x1 = (-bb1 - aa1 * yTop).toInt()
            val x2 = (-bb2 - aa2 * yTop).toInt()
            rectLst.add(TextRectDetails(Rect(x1, yTop, x2, yBottom)))
            yTop += tfd.lineHeight
        }
        return rectLst
    }
}
