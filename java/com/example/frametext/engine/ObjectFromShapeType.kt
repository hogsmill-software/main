package com.example.frametext.engine

import android.graphics.Canvas
import android.graphics.Paint
import com.example.frametext.engine.mainShapes.*
import com.example.frametext.engine.mainSizes.HeartMainSizes
import com.example.frametext.engine.mainSizes.MainSizes
import com.example.frametext.engine.mainSizes.SquareMainSizes
import com.example.frametext.engine.textBoundaries.*
import com.example.frametext.enums.MainShapeType
import com.example.frametext.shapes.edge.EdgeShapeDetails

object ObjectFromShapeType {
    fun getMainSizeFromShapeType(st: MainShapeType?, margin: Int): MainSizes {
        when (st) {
            MainShapeType.Heart -> return HeartMainSizes(margin)
            MainShapeType.Square, MainShapeType.Circle, MainShapeType.Diamond -> return SquareMainSizes(margin)
            else -> {}
        }
        // Return a square -this could be wrong, but alternative if return null is potential crash. Best option is let's default to that - not perfect admittedly.
        return SquareMainSizes(margin)
    }

    fun getTextBoundariesFromShapeType(
        st: MainShapeType?,
        paint: Paint,
        mainSizes: MainSizes,
        sd: EdgeShapeDetails,
        tfd: TextFormattingDetails
    ): TextBoundaries {
        when (st) {
            MainShapeType.Heart -> if (mainSizes is HeartMainSizes) {
                return HeartTextBoundaries(paint, mainSizes, sd, tfd)
            }
            MainShapeType.Square -> if (mainSizes is SquareMainSizes) {
                return SquareTextBoundaries(paint, mainSizes, sd, tfd)
            }
            MainShapeType.Circle -> if (mainSizes is SquareMainSizes) {
                return CircleTextBoundaries(paint, mainSizes, sd, tfd)
            }
            MainShapeType.Diamond -> if (mainSizes is SquareMainSizes) {
                return DiamondTextBoundaries(paint, mainSizes, sd, tfd)
            }
            else -> {}
        }
        // OK right now as only have SquareMainSizes and HeartMainSizes
        return if (mainSizes is HeartMainSizes) HeartTextBoundaries(paint, mainSizes, sd, tfd)
        else SquareTextBoundaries(paint, mainSizes as SquareMainSizes, sd, tfd)
    }

    fun getMainShape(
        st: MainShapeType,
        canvas: Canvas,
        mainSizes: MainSizes,
        closestDistance: Int,
        sd: EdgeShapeDetails
    ): MainShape? {
        when (st) {
            MainShapeType.Heart -> if (mainSizes is HeartMainSizes) {
                return HeartMainShape(canvas, mainSizes, closestDistance, sd)
            }
            MainShapeType.Square -> if (mainSizes is SquareMainSizes) {
                return SquareMainShape(canvas, mainSizes, closestDistance, sd)
            }
            MainShapeType.Circle -> if (mainSizes is SquareMainSizes) {
                return CircleMainShape(canvas, mainSizes, closestDistance, sd)
            }
            MainShapeType.Diamond -> if (mainSizes is SquareMainSizes) {
                return DiamondMainShape(canvas, mainSizes, closestDistance, sd)
            }
            else -> {}
        }
        return null
    }
}
