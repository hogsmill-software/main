package com.example.frametext.userControls

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.frametext.R
import com.example.frametext.enums.MainShapeType
import com.example.frametext.helpers.Utilities
import com.example.frametext.shapes.main.*

class MainShapeCellCtrl : View {
    private val paint = Paint()
    private var showBorder = false
    private var isMainShapeSelected = false
    private var shapeType: MainShapeType = MainShapeType.None
    private var drawShapeDetails: MainShape? = null
    private var innerShapeBorder = 0f
    var isMainButton = false // Indicates main button and not in pop up

    constructor(context: Context) : super(context) {
        initStandardSizes(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initStandardSizes(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initStandardSizes(context)
    }

    constructor(
        context: Context,
        shapeType: MainShapeType,
        showBorder: Boolean
    ) : super(context) {
        initStandardSizes(context)
        this.shapeType = shapeType
        this.showBorder = showBorder
        isMainShapeSelected = false
        createShape(context)
    }

    private fun createShape(context: Context) {
        var innerShapeWidthDp = 24f
        var innerShapeBorderDp = 2f
        if (shapeType === MainShapeType.Square) {
            innerShapeWidthDp = 20f
            innerShapeBorderDp = 4f
        }
        val innerShapeWidth = context.let { Utilities.convertDpToPixel(innerShapeWidthDp, it) }
            .toInt()
        innerShapeBorder = Utilities.convertDpToPixel(innerShapeBorderDp, context)

        when (shapeType) {
            MainShapeType.Heart -> drawShapeDetails =
                HeartMainShape(ContextCompat.getColor(context, R.color.black), innerShapeWidth)
            MainShapeType.Circle -> drawShapeDetails =
                CircleMainShape(ContextCompat.getColor(context, R.color.black), innerShapeWidth)
            MainShapeType.Square -> drawShapeDetails =
                SquareMainShape(ContextCompat.getColor(context, R.color.black), innerShapeWidth)
            MainShapeType.Diamond -> drawShapeDetails =
                DiamondMainShape(ContextCompat.getColor(context, R.color.black), innerShapeWidth)
            else -> {}
        }
    }

    fun setShapeType(shapeType: MainShapeType) {
        this.shapeType = shapeType
        createShape(this.context)
        invalidate()
    }

    fun getShapeType(): MainShapeType {
        return shapeType
    }

    val size: Int
        get() = Companion.size.toInt()

    override fun setSelected(selected: Boolean) {
        isMainShapeSelected = selected
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (showBorder) {
            paint.color = ContextCompat.getColor(context, R.color.highlightBlue)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderWidth
            canvas.drawRect(rcBounds, paint)
        }
        if (isMainShapeSelected) {
            paint.color = ContextCompat.getColor(context, R.color.faintHighlightBlue)
            paint.style = Paint.Style.FILL
            canvas.drawRect(rcBounds, paint)
            paint.color = ContextCompat.getColor(context, R.color.highlightBlue)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderWidth
            canvas.drawRect(rcBounds, paint)
        }
        if (drawShapeDetails != null) {
            val shapeColor = ContextCompat.getColor(context, if (isMainButton || isMainShapeSelected) R.color.black else (Utilities.getTextColorId(context)))
            drawShapeDetails!!.setColor(shapeColor)
        }
        drawShapeType(canvas)
    }

    private fun drawShapeType(canvas: Canvas) {
        if (drawShapeDetails != null) {
            paint.color = drawShapeDetails!!.getColor()
            paint.style = Paint.Style.STROKE
            val shapeWidth = Utilities.convertDpToPixel(3f, this.context).toInt()
            paint.strokeWidth = shapeWidth.toFloat()

            val shapeHeight: Float = if (shapeType === MainShapeType.Heart) {
                -drawShapeDetails!!.getHeight() + 1.5f * innerShapeBorder
            } else {
                -drawShapeDetails!!.getHeight() + innerShapeBorder
            }
            drawShapeDetails!!.draw(canvas, innerShapeBorder, shapeHeight, paint)

            paint.color = ContextCompat.getColor(context, R.color.highlightBlue)
            val writingWidth = Utilities.convertDpToPixel(1f, this.context).toInt()
            paint.strokeWidth = writingWidth.toFloat()
            drawShapeDetails!!.drawWriting(canvas, innerShapeBorder, shapeHeight, paint)
            paint.color = drawShapeDetails!!.getColor()
            paint.strokeWidth = shapeWidth.toFloat()
            drawShapeDetails!!.draw(canvas, innerShapeBorder, shapeHeight, paint)
        }
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //
        setMeasuredDimension(Companion.size.toInt(), Companion.size.toInt())
    }

    companion object {
        private var size = 0f
        private var borderWidth = 0f
        private lateinit var rcBounds: RectF
        private var initialized = false
        private fun initStandardSizes(context: Context) {
            if (!initialized) {
                initialized = true
                size = Utilities.convertDpToPixel(28f, context)
                borderWidth = Utilities.convertDpToPixel(1f, context)
                rcBounds = RectF(0f, 0f, size, size)
            }
        }
    }
}
