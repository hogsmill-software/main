package com.example.frametext.shapes.edge

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import kotlin.properties.Delegates

class DrawStarEdgeShape(widthHeight: Int, override var color: Int) : ColoredEdgeShapeDetails {
    override var width: Float = widthHeight.toFloat()
        private set
    override var height: Float = widthHeight.toFloat()
        private set
    override var centerX by Delegates.notNull<Float>()
        private set
    override var centerY by Delegates.notNull<Float>()
        private set
    override var bottomAdjustment: Float = -2 * height
        private set

    private var starSearchGloObj: StarDetailsGlobalObject? = null
    override fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        var yy = y
        yy += height
        val pts: ArrayList<PointF> = starSearchGloObj!!.starPtsList
        if (pts.size > 1) {
            val path = Path()
            val firstPt = pts[0]
            path.moveTo(x + firstPt.x, yy + firstPt.y)
            for (i in 1 until pts.size) {
                val pt = pts[i]
                path.lineTo(x + pt.x, yy + pt.y)
            }
            canvas.drawPath(path, paint)
        }
    }

    companion object {
        private val starDetailsMap = HashMap<StarSearchDetails, StarDetailsGlobalObject?>()
    }

    init {
        val spikes = 5
        val startAngle = -Math.PI / 2.0
        val starSearchDetails =
            StarSearchDetails(widthHeight.toFloat(), 0.25f * widthHeight, startAngle, spikes)
        height = widthHeight.toFloat()
        if (starDetailsMap.containsKey(starSearchDetails)) {
            starSearchGloObj = starDetailsMap[starSearchDetails]
        } else {
            starSearchGloObj = StarDetailsGlobalObject(starSearchDetails)
            starDetailsMap[starSearchDetails] = starSearchGloObj
        }

        centerX = starSearchGloObj!!.centerX
        centerY = starSearchGloObj!!.centerY
    }
}

