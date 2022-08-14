package com.example.frametext.engine

import android.graphics.Canvas
import android.graphics.Paint
import com.example.frametext.engine.mainShapes.CircleMainShape
import com.example.frametext.engine.mainShapes.HeartMainShape
import com.example.frametext.engine.mainShapes.MainShape
import com.example.frametext.engine.mainShapes.SquareMainShape
import com.example.frametext.engine.mainSizes.HeartMainSizes
import com.example.frametext.engine.mainSizes.MainSizes
import com.example.frametext.engine.mainSizes.SquareMainSizes
import com.example.frametext.engine.textBoundaries.CircleTextBoundaries
import com.example.frametext.engine.textBoundaries.HeartTextBoundaries
import com.example.frametext.engine.textBoundaries.SquareTextBoundaries
import com.example.frametext.engine.textBoundaries.TextBoundaries
import com.example.frametext.enums.MainShapeType
import com.example.frametext.shapes.edge.EdgeShapeDetails

object ObjectFromShapeType {
    fun getMainSizeFromShapeType(st: MainShapeType?, margin: Int): MainSizes? {
        when (st) {
            MainShapeType.Heart -> return HeartMainSizes(margin)
            MainShapeType.Square, MainShapeType.Circle -> return SquareMainSizes(margin)
            else -> {}
        }
        return null
    }

    fun getTextBoundariesFromShapeType(
        st: MainShapeType?,
        paint: Paint,
        mainSizes: MainSizes,
        sd: EdgeShapeDetails,
        tfd: TextFormattingDetails
    ): TextBoundaries? {
        when (st) {
            MainShapeType.Heart -> {
                if (mainSizes is HeartMainSizes) {
                    return HeartTextBoundaries(paint, mainSizes, sd, tfd)
                }
                if (mainSizes is SquareMainSizes) {
                    return SquareTextBoundaries(paint, mainSizes, sd, tfd)
                }
                if (mainSizes is SquareMainSizes) {
                    return CircleTextBoundaries(paint, mainSizes, sd, tfd)
                }
            }
            MainShapeType.Square -> {
                if (mainSizes is SquareMainSizes) {
                    return SquareTextBoundaries(paint, mainSizes, sd, tfd)
                }
                if (mainSizes is SquareMainSizes) {
                    return CircleTextBoundaries(paint, mainSizes, sd, tfd)
                }
            }
            MainShapeType.Circle -> if (mainSizes is SquareMainSizes) {
                return CircleTextBoundaries(paint, mainSizes, sd, tfd)
            }
            else -> {}
        }
        return null
    }

    fun getMainShape(
        st: MainShapeType,
        canvas: Canvas,
        mainSizes: MainSizes,
        closestDistance: Int,
        sd: EdgeShapeDetails
    ): MainShape? {
        when (st) {
            MainShapeType.Heart -> {
                if (mainSizes is HeartMainSizes) {
                    return HeartMainShape(canvas, mainSizes, closestDistance, sd)
                }
                if (mainSizes is SquareMainSizes) {
                    return SquareMainShape(
                        canvas,
                        mainSizes,
                        closestDistance,
                        sd
                    )
                }
                if (mainSizes is SquareMainSizes) {
                    return CircleMainShape(
                        canvas,
                        mainSizes,
                        closestDistance,
                        sd
                    )
                }
            }
            MainShapeType.Square -> {
                if (mainSizes is SquareMainSizes) {
                    return SquareMainShape(
                        canvas,
                        mainSizes,
                        closestDistance,
                        sd
                    )
                }
                if (mainSizes is SquareMainSizes) {
                    return CircleMainShape(
                        canvas,
                        mainSizes,
                        closestDistance,
                        sd
                    )
                }
            }
            MainShapeType.Circle -> if (mainSizes is SquareMainSizes) {
                return CircleMainShape(canvas, mainSizes, closestDistance, sd)
            }
            else -> {}
        }
        return null
    }
}
