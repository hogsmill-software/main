package com.example.frametext.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import com.example.frametext.R
import com.example.frametext.engine.mainShapes.MainShape
import com.example.frametext.engine.mainSizes.MainSizes
import com.example.frametext.enums.MainShapeType
import com.example.frametext.shapes.edge.EdgeShapeDetails


// https://stackoverflow.com/questions/27588965/how-to-use-custom-font-in-a-project-written-in-android-studio
class ImageGenerator(
    private val tfd: TextFormattingDetails,
    mainShapeType: MainShapeType,
    sd: EdgeShapeDetails,
    backgroundColor: Int,
    margin: Int,
    minDistEdgeShape: Int,
    context: Context
) {
    var bitmap: Bitmap
        private set

    private var paint: Paint
    private var canvas: Canvas
    private val sd: EdgeShapeDetails
    private var dt: DrawText? = null
    private var pixelTxtLen = 0f
    private val mainSizes: MainSizes
    private val backgroundColor: Int
    private var context: Context
    private val mainShapeType: MainShapeType
    private val minDistEdgeShape: Int




    // part of experiment - used for computing gross estimate
    private fun textLenFromWidth(width: Int): Int {
        //hd = new HeartDetails(/*canvas*/);
        mainSizes.resetSizes(width)
        dt = DrawText(canvas, mainSizes, sd, tfd, mainShapeType, context)
        return dt?.computeTextSpaceAvailable() ?: 0
    }

    private fun computeWidthEstimateFromTextLen(
        lowerBound: Int,
        upperBound: Int,
        step: Int,
        txtLengthEstimate: Float
    ): Int {
        var retVal = -1
        var i = lowerBound
        while (i <= upperBound) {
            val txtLen = textLenFromWidth(i)
            if (txtLen >= txtLengthEstimate) {
                retVal = if (step == 1) {
                    i
                } else {
                    computeWidthEstimateFromTextLen(i - step, i, step / 4, txtLengthEstimate)
                }
                break
            }
            i += step
        }
        return retVal
    }

    // Retrieves a gross estimate of with from pixel text length
    private fun getWidthEstimateFromTextLen(txtLengthEstimate: Float): Int {
        var step = stepIncrement
        while (step < txtLengthEstimate) {
            step *= stepIncrement
        }
        val lowerBound = 0
        val upperBound = step
        step /= stepIncrement
        return computeWidthEstimateFromTextLen(lowerBound, upperBound, step, txtLengthEstimate)
    }

    @Throws(FrameTextException::class)
    fun computeTextFit(context: Context) {
        mainSizes.resetSizes(getWidthEstimateFromTextLen(pixelTxtLen))
        this.context = context
        if (tfd.contentText.isEmpty()) {
            throw FrameTextException(context.resources.getString(R.string.error_no_text))
        } else if (mainSizes.width == 0 || mainSizes.height == 0) {
            throw FrameTextException(context.resources.getString(R.string.error_text_too_short))
        }
        var goodSize = false
        var counter = 0
        var decreased = false
        var increased = false
        var smallDecrease = false
        var smallIncrease = false
        var bestOptimization = false
        canvas = Canvas(bitmap)
        do {
            counter++
            if (mainSizes.width == 0 || mainSizes.height == 0) throw FrameTextException(
                context.resources.getString(R.string.error_no_width_or_height)
            )
            dt = DrawText(canvas, mainSizes, sd, tfd, mainShapeType, context)

            if (counter == 1) {
                dt?.resetTextInputDetails()
            }
            dt?.computeTextPlacementDetails()
            if (bestOptimization) break
            if (dt?.doesAllTextFit() == true) {
                if (dt?.sizeOptimized() == true) {
                    goodSize = true
                } else {
                    // Frame is too big. We need to decrease it and try again...
                    if (increased) {
                        if (smallIncrease) {
                            // this is best size we can get. Any smaller, text won't fit...
                            goodSize = true
                        } else {
                            mainSizes.resetSizes(mainSizes.width - 1)
                            smallDecrease = true
                        }
                    } else {
                        mainSizes.resetSizes(mainSizes.width - 5)
                    }
                    decreased = true
                }
            } else {
                // Frame is too small. We need to increase it and try again...
                if (decreased) {
                    mainSizes.resetSizes(mainSizes.width + 1)
                    smallIncrease = true
                    if (smallDecrease) {
                        // both small increase and decrease have been call - this is best optimisation we can get.
                        // exit loop after this...
                        bestOptimization = true
                    }
                } else {
                    mainSizes.resetSizes(mainSizes.width + 5)
                }
                increased = true
            }
        } while (!goodSize && counter < maxIterations)
        bitmap.recycle()
        bitmap = Bitmap.createBitmap(
            mainSizes.width,
            mainSizes.height,
            Bitmap.Config.ARGB_8888
        )
        canvas = Canvas(bitmap)
        dt = DrawText(canvas, mainSizes, sd, tfd, mainShapeType, context)
        dt?.computeTextPlacementDetails()
    }

    fun draw() {
        // We work out this distance later, which will be a bit greater.
        paint.color = backgroundColor
        canvas.drawRect(0f, 0f, mainSizes.width.toFloat(), mainSizes.height.toFloat(), paint)

        /* for testing emoji placement
		if (sd instanceof EmojiShapeDetails) {
			EmojiShapeDetails esd = (EmojiShapeDetails)sd;
			esd.drawEmojiWithBoundaries(canvas, paint);
		}
		*/

        // Draw hearts...
        val ms: MainShape? = ObjectFromShapeType.getMainShape(
            mainShapeType,
            canvas, mainSizes, minDistEdgeShape, sd
        )
        ms?.draw()

        // for testing so see where bounding rectangles are...
        // dt.drawTextBoundingRectangles();
        dt?.draw()
    }

    companion object {
        private const val maxIterations = 300
        private const val stepIncrement = 4
    }

    init {
        mainSizes = ObjectFromShapeType.getMainSizeFromShapeType(mainShapeType, margin)
        this.sd = sd
        this.backgroundColor = backgroundColor
        this.context = context
        this.mainShapeType = mainShapeType
        this.minDistEdgeShape = minDistEdgeShape
        // Initialises an unused graphic object so can calculate text size.
        // We don't know the size of the image till we have the text size.
        // We can only have this by creating an unused graphic object.
        bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)

        val tf = Typeface.create("TimesRoman", Typeface.NORMAL)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.typeface = tf
        paint.textSize = 150f

        // Computes the pixel text length. (Not number of characters)
        pixelTxtLen = paint.measureText(tfd.contentText)
    }
}