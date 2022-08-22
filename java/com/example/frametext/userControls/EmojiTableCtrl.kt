package com.example.frametext.userControls

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.frametext.R
import com.example.frametext.helpers.EmojiHelper
import com.example.frametext.helpers.Utilities
import kotlin.math.abs

class EmojiTableCtrl : View, View.OnClickListener {
    private var borderThickness = 0f
    private var borderMargin = 0f
    private var selectedEmojiCtrl: EmojiCellCtrl? = null
    private val paint = Paint()
    private var boundingRect: RectF? = null
    private val emojiCellCtrlList = ArrayList<EmojiCellCtrl>()
    private var popUpSize: Point? = null
    private var selectEmoji: String? = null
    private val rcBounds = Rect()
    private var verticalGapHeight = 0f
    private var popUpBound: RectF? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        var emojiCount = 0
        var i = 0
        val emojiList =
            "❤️\uD83E\uDDE1\uD83D\uDC9B\uD83D\uDC9A\uD83D\uDC99\uD83D\uDC9C\uD83D\uDDA4\uD83E\uDD0D\uD83E\uDD0E\uD83D\uDC94\uD83D\uDC8C️\uD83D\uDC95\uD83D\uDC9E\uD83D\uDC93\uD83D\uDC97\uD83D\uDC96\uD83D\uDC98\uD83D\uDC9D\uD83D\uDC9F\uD83D\uDD34\uD83D\uDFE0\uD83D\uDFE1\uD83D\uDFE2\uD83D\uDD35\uD83D\uDFE3⚪⚫\uD83D\uDFE4\uD83D\uDD3A\uD83D\uDD38\uD83D\uDD39\uD83D\uDD36\uD83D\uDD37\uD83D\uDFE5\uD83D\uDFE7\uD83D\uDFE8\uD83D\uDFE9\uD83D\uDFE6\uD83D\uDFEA⬛⬜\uD83D\uDFEB♠️♣️♥️♦️\uD83D\uDCA7\uD83E\uDE78⭐\uD83C\uDF1F✨\uD83D\uDCA5\uD83D\uDD25☀️"
        while (i < emojiList.length) {
            val chr = emojiList[i]
            var singleEmojiString: String?
            if (EmojiHelper.isCharEmojiAtPos(emojiList, i)) {
                val len: Int = EmojiHelper.emojiLengthAtPos(emojiList, i)
                singleEmojiString = emojiList.substring(i, i + len)
                i += len
            } else {
                singleEmojiString = chr.toString()
                i++
            }
            if (chr.code != 65039) {
                emojiCount++
                val ecc = EmojiCellCtrl(context, singleEmojiString, false)
                emojiCellCtrlList.add(ecc)
            }
        }
        val emojiCtrl = emojiCellCtrlList[0]
        size = emojiCtrl.size
        val rows: Int =
            emojiCount / columns + if (emojiCount % columns != 0) 1 else 0
        borderThickness = Utilities.convertDpToPixel(2f, context)
        borderMargin = Utilities.convertDpToPixel(3f, context)
        val halfThickness = borderThickness / 2
        boundingRect = RectF(
            halfThickness,
            rows * size - halfThickness + 2 * borderMargin,
            columns * size - halfThickness + 2 * borderMargin,
            halfThickness
        )
        popUpSize = Utilities.getRealScreenSize(context)
        //   public static Point
        val tf = Typeface.create("TimesRoman", Typeface.NORMAL)
        paint.typeface = tf
        paint.textSize = Utilities.convertDpToPixel(40f, getContext())
        selectEmoji = resources.getString(R.string.select_emoji)
        paint.getTextBounds(selectEmoji, 0, selectEmoji!!.length, rcBounds)
        verticalGapHeight =
            (popUpSize!!.y - abs(boundingRect!!.height()) - abs(rcBounds.height())) / 3.0f
        popUpBound = RectF(0f, 0f, popUpSize!!.x.toFloat(), popUpSize!!.y.toFloat())
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = ContextCompat.getColor(context, R.color.white)
        paint.style = Paint.Style.FILL
        canvas.drawRect(popUpBound!!, paint)
        paint.color = ContextCompat.getColor(context, R.color.black)
        canvas.drawText(
            selectEmoji!!,
            (popUpSize!!.x - rcBounds.width()) / 2.0f,
            verticalGapHeight + rcBounds.height(),
            paint
        )
        canvas.translate(
            (popUpSize!!.x - boundingRect!!.width()) / 2.0f,
            2 * verticalGapHeight + rcBounds.height()
        )
        paint.color = ContextCompat.getColor(context, R.color.midDayFog)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderThickness
        canvas.drawRect(boundingRect!!, paint)
        canvas.translate(borderMargin, borderMargin)
        for (i in emojiCellCtrlList.indices) {
            emojiCellCtrlList[i].draw(canvas)
            if (i % columns == lastColumn) {
                canvas.translate(
                    (-lastColumn * size).toFloat(),
                    size.toFloat()
                )
            } else {
                canvas.translate(size.toFloat(), 0f)
            }
        }
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(popUpSize!!.x, popUpSize!!.y)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event) // this super call is important !!!
        var success = false
        val x = event.x - (popUpSize!!.x - boundingRect!!.width()) / 2.0f
        val y = event.y - (2 * verticalGapHeight + rcBounds.height())
        val col: Int = (x - borderMargin).toInt() / size
        val row: Int = (y - borderMargin).toInt() / size
        val pos: Int = columns * row + col

        // Click on bottom border and get crash without if statement as out of range.
        // Click on right border and leftmost next item selected without columns check.
        if (pos < emojiCellCtrlList.size && col < columns && pos >= 0) {
            selectedEmojiCtrl!!.setEmoji(emojiCellCtrlList[pos].emoji)
            success = performClick()
        }
        return success
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onClick(v: View) {}
    fun setSelectedEmojiCtrl(selectedEmojiCtrl: EmojiCellCtrl?) {
        this.selectedEmojiCtrl = selectedEmojiCtrl
        var selectedEmoji = ""
        if (selectedEmojiCtrl != null) {
            selectedEmoji = selectedEmojiCtrl.emoji
        }
        for (idx in emojiCellCtrlList.indices) {
            val ecc = emojiCellCtrlList[idx]
            if (ecc.emoji.compareTo(selectedEmoji) == 0) {
                ecc.isSelected = true
            }
        }
    }

    companion object {
        private var size = 0
        private const val columns = 6
        private const val lastColumn: Int = columns - 1
    }
}