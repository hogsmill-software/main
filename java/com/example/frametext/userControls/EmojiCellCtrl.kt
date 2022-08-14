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
import com.example.frametext.helpers.Utilities

class EmojiCellCtrl : View {
    private var emoji = 0x2665.toChar().toString()
    private val paint = Paint()
    private var showBorder = false
    private var _isSelected = false
    private var isActive = true

    constructor(context: Context?) : super(context) {
        if (context != null) {
            initStandardSizes(context)
        }
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        if (context != null) {
            initStandardSizes(context)
        }
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        if (context != null) {
            initStandardSizes(context)
        }
    }

    constructor(context: Context?, emoji: String, showBorder: Boolean) : super(context) {
        if (context != null) {
            initStandardSizes(context)
        }
        this.emoji = emoji
        this.showBorder = showBorder
        _isSelected = false
    }

    fun setEmoji(emoji: String) {
        this.emoji = emoji
        invalidate()
    }

    fun getEmoji(): String {
        return emoji
    }

    override fun setSelected(selected: Boolean) {
        _isSelected = selected
    }

    val size: Int
        get() = EmojiCellCtrl.size.toInt()

    fun setIsActive(isActive: Boolean) {
        this.isActive = isActive
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (showBorder) {
            paint.color = ContextCompat.getColor(context, R.color.highlightBlue)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderWidth
            boundingRect?.let { canvas.drawRect(it, paint) }
        }
        if (_isSelected) {
            paint.color = ContextCompat.getColor(context, R.color.faintHighlightBlue)
            paint.style = Paint.Style.FILL
            boundingRect?.let { canvas.drawRect(it, paint) }
            paint.color = ContextCompat.getColor(context, R.color.highlightBlue)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderWidth
            boundingRect?.let { canvas.drawRect(it, paint) }
        }
        if (!isActive) {
            paint.color = ContextCompat.getColor(context, R.color.midDayFog)
            paint.style = Paint.Style.FILL
            boundingRect?.let { canvas.drawRect(it, paint) }
            paint.color = ContextCompat.getColor(context, R.color.translucide)
        } else {
            paint.color = ContextCompat.getColor(context, R.color.black)
        }
        val tf = Typeface.create("TimesRoman", Typeface.NORMAL)
        paint.typeface = tf
        paint.textSize = txtSize
        canvas.drawText(emoji, 0f, txtBaseLinePos, paint)
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //
        setMeasuredDimension(size, size)
    }

    companion object {
        private var size: Float = 0f
        private var txtSize = 0f
        private var txtBaseLinePos = 0f
        private var borderWidth = 0f
        private var boundingRect: RectF? = null
        private fun initStandardSizes(context: Context) {
            size = Utilities.convertDpToPixel(28f, context)
            txtSize = Utilities.convertDpToPixel(22.5f, context)
            txtBaseLinePos = Utilities.convertDpToPixel(21.5f, context)
            borderWidth = Utilities.convertDpToPixel(2f, context)
            if (boundingRect == null) {
                boundingRect =
                    RectF(0f, 0f, size, size)
            }
        }
    }
}
