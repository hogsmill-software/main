package com.example.frametext.userControls.colorPicker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.frametext.R
import com.example.frametext.helpers.Utilities

class ColorCellCtrl: View  {
    private val paint = Paint()
    private var colorSelected = false
    var color: Int = 0
        private set

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

    constructor(context: Context, colorId: Int) : super(context) {
        initStandardSizes(context)
        this@ColorCellCtrl.color = ContextCompat.getColor(context, colorId)
    }

    val size: Float
        get() = ColorCellCtrl.size

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        colorSelected = selected
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        boundingRect?.let { canvas.drawRect(it, paint) }

        if (colorSelected) {
            paint.color = ContextCompat.getColor(context, R.color.highlightBlue)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = selectFrameWidth
            selectFrameRect?.let { canvas.drawRect(it, paint) }
        }

        paint.color = color
        paint.style = Paint.Style.FILL
        colorRect?.let { canvas.drawRect(it, paint) }

        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = colorFrmWidth
        colorFrmRect?.let { canvas.drawRect(it, paint) }
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(size.toInt(), size.toInt())
    }

    companion object {
        var size = 0f
        private set
        private var selectFrameWidth = 0f
        private var boundingRect: RectF? = null
        private var selectFrameRect: RectF? = null
        private var colorRect: RectF? = null
        private var colorFrmWidth = 0f
        private var colorFrmRect: RectF? = null

        private fun initStandardSizes(context: Context) {
            if (boundingRect == null) {
                size = Utilities.convertDpToPixel(40f, context)
                selectFrameWidth = Utilities.convertDpToPixel(2f, context)
                boundingRect = RectF(0f, 0f, size, size)
                selectFrameRect = RectF(selectFrameWidth/2.0f, selectFrameWidth/2.0f, size - selectFrameWidth/2.0f, size - selectFrameWidth/2.0f)
                val innerMargin = Utilities.convertDpToPixel(6f, context)
                colorRect = RectF(innerMargin, innerMargin, size - innerMargin, size - innerMargin)
                colorFrmWidth = Utilities.convertDpToPixel(1f, context)
                val colorFrmMargin = innerMargin + colorFrmWidth
                colorFrmRect = RectF(colorFrmMargin, colorFrmMargin, size - colorFrmMargin, size - colorFrmMargin)
            }
        }
    }
}