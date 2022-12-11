package com.example.frametext.userControls

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.frametext.R
import com.example.frametext.enums.SymbolShapeType
import com.example.frametext.helpers.Utilities
import com.example.frametext.shapes.edge.*

class ShapeCellCtrl : View {
    private val paint = Paint()
    private var showBorder = false
    private var _isSelected = false
    private var shapeType: SymbolShapeType = SymbolShapeType.None
    private var symbol: String? = null
    private var drawShapeDetails: ColoredEdgeShapeDetails? = null
    private var innerShapeBorder = 0f
    private var isActive = true

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

    constructor(context: Context, symbol: String?, showBorder: Boolean) : super(context) {
        initStandardSizes(context)
        shapeType = SymbolShapeType.None
        this.symbol = symbol
        this.showBorder = showBorder
        _isSelected = false
    }

    constructor(context: Context, shapeType: SymbolShapeType, showBorder: Boolean) : super(context) {
        initStandardSizes(context)
        this.shapeType = shapeType
        symbol = null
        this.showBorder = showBorder
        _isSelected = false
        createShape(context)
    }

    private fun createShape(context: Context) {
        var innerShapeWidthDp = 24f
        var innerShapeBorderDp = 2f
        if (shapeType === SymbolShapeType.Square) {
            innerShapeWidthDp = 20f
            innerShapeBorderDp = 4f
        }
        val innerShapeWidth = Utilities.convertDpToPixel(innerShapeWidthDp, context).toInt()
        innerShapeBorder = Utilities.convertDpToPixel(innerShapeBorderDp, context)
        when (shapeType) {
            SymbolShapeType.Heart -> drawShapeDetails =
                DrawHeartEdgeShape(innerShapeWidth, ContextCompat.getColor(context, R.color.black))
            SymbolShapeType.Circle -> drawShapeDetails =
                DrawCircleEdgeShape(innerShapeWidth, ContextCompat.getColor(context, R.color.black))
            SymbolShapeType.Square -> drawShapeDetails =
                DrawSquareEdgeShape(innerShapeWidth, ContextCompat.getColor(context, R.color.black))
            SymbolShapeType.Smiley -> drawShapeDetails =
                DrawSmileyEdgeShape(innerShapeWidth, ContextCompat.getColor(context, R.color.black))
            SymbolShapeType.Star -> drawShapeDetails =
                DrawStarEdgeShape(innerShapeWidth, ContextCompat.getColor(context, R.color.black))
            SymbolShapeType.Spade -> drawShapeDetails =
                DrawSpadeEdgeShape(innerShapeWidth, ContextCompat.getColor(context, R.color.black))
            SymbolShapeType.Club -> drawShapeDetails =
                DrawClubEdgeShape(innerShapeWidth, ContextCompat.getColor(context, R.color.black))
            SymbolShapeType.Diamond -> drawShapeDetails =
                DrawDiamondEdgeShape(innerShapeWidth, ContextCompat.getColor(context, R.color.black))
            else -> {}
        }
    }

    fun setShapeType(shapeType: SymbolShapeType) {
        this.shapeType = shapeType
        symbol = null
        createShape(this.context)
        invalidate()
    }

    fun setSymbol(symbol: String?) {
        shapeType = SymbolShapeType.None
        drawShapeDetails = null
        this.symbol = symbol
        invalidate()
    }

    fun getShapeType(): SymbolShapeType {
        return shapeType
    }

    fun getSymbol(): String? {
        return symbol
    }

    val size: Int
        get() = ShapeCellCtrl.size

    override fun setSelected(selected: Boolean) {
        _isSelected = selected
    }

    fun setIsActive(isActive: Boolean) {
        this.isActive = isActive
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (showBorder) {
            paint.color = ContextCompat.getColor(context, R.color.highlightBlue)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderWidth.toFloat()
            boundingRect?.let { canvas.drawRect(it, paint) }
        }
        if (_isSelected) {
            paint.color = ContextCompat.getColor(context, R.color.faintHighlightBlue)
            paint.style = Paint.Style.FILL
            boundingRect?.let { canvas.drawRect(it, paint) }
            paint.color = ContextCompat.getColor(context, R.color.highlightBlue)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderWidth.toFloat()
            boundingRect?.let { canvas.drawRect(it, paint) }
        }
        if (!isActive) {
            paint.color = ContextCompat.getColor(context, R.color.midDayFog)
            paint.style = Paint.Style.FILL
            boundingRect?.let { canvas.drawRect(it, paint) }
        }
        val shapeColor = ContextCompat.getColor(context, if (isActive) R.color.black else R.color.fog)
        if (drawShapeDetails != null) {
            drawShapeDetails!!.color = shapeColor
            drawShapeType(canvas)
        } else if (symbol != null) {
            paint.style = Paint.Style.FILL
            paint.color = shapeColor
            val tf = Typeface.create("TimesRoman", Typeface.NORMAL)
            paint.typeface = tf
            paint.textSize = txtSize.toFloat()
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(
                symbol!!,
                txtStartPos.toFloat(),
                txtBaseLinePos.toFloat(),
                paint
            )
        }
    }

    private fun drawShapeType(canvas: Canvas) {
        if (drawShapeDetails != null) {
            paint.color = drawShapeDetails!!.color
            paint.style = Paint.Style.FILL
            val shapeHeight: Float = if (shapeType === SymbolShapeType.Heart) {
                -drawShapeDetails!!.height + 1.5f * innerShapeBorder
            } else if (shapeType === SymbolShapeType.Star) {
                -drawShapeDetails!!.height + 1.5f * innerShapeBorder
            } else {
                -drawShapeDetails!!.height + innerShapeBorder
            }
            drawShapeDetails!!.draw(canvas, innerShapeBorder, shapeHeight, paint)
        }
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //
        setMeasuredDimension(size, size)
    }

    companion object {
        private var size = 0
        private var borderWidth = 0
        private var txtSize = 0
        private var txtBaseLinePos = 0
        private var txtStartPos = 0
        private var boundingRect: RectF? = null
        private fun initStandardSizes(context: Context) {
            size = Utilities.convertDpToPixel(28f, context).toInt()
            borderWidth = Utilities.convertDpToPixel(1f, context).toInt()
            txtSize = Utilities.convertDpToPixel(22.5f, context).toInt()
            txtBaseLinePos = Utilities.convertDpToPixel(21.5f, context).toInt()
            txtStartPos = Utilities.convertDpToPixel(14f, context).toInt()
            if (boundingRect == null) {
                boundingRect =
                    RectF(0f, 0f, size.toFloat(),
                        size.toFloat()
                    )
            }
        }
    }
}