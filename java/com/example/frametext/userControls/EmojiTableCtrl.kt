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
import com.example.frametext.userControls.colorPicker.Constants
import kotlin.math.max
import kotlin.math.floor

class EmojiTableCtrl : View, View.OnClickListener {
    private var borderThickness = 0f
    private var borderMargin = 0f
    private lateinit var selectedEmojiCtrl: EmojiCellCtrl
    private val paint = Paint()
    private lateinit var rcEmojisBounds: RectF // The emojis bounding rectangle
    private val emojiCellCtrlList = ArrayList<EmojiCellCtrl>()
    private lateinit var selectEmoji: String
    private lateinit var rcHeaderBounds: Rect
    private lateinit var rcFullScreenBounds: RectF
    private lateinit var rcPopupBounds: RectF
    private var popupMargin = 0f
    private var headerEmojisGap = 0f // Gap between header and emojis below.
    private var emojisHorizontalGap = 0f // Horizontal gap between the emoji buttons
    private var emojisVerticalGap = 0f // Vertical gap between the emoji buttons
    private lateinit var popUpHeader: PopupHeader

    constructor(context: Context, purchasedMore: Boolean) : super(context) {
        init(context, purchasedMore)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, false)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, false)
    }

    private fun init(context: Context, purchasedMore: Boolean) {
        var emojiCount = 0
        var i = 0
        val emojiList =
            if (purchasedMore) "❤️\uD83E\uDDE1\uD83D\uDC9B\uD83D\uDC9A\uD83D\uDC99\uD83D\uDC9C\uD83D\uDDA4\uD83E\uDD0D\uD83E\uDD0E\uD83D\uDC94\uD83D\uDC8C️\uD83D\uDC95\uD83D\uDC9E\uD83D\uDC93\uD83D\uDC97\uD83D\uDC96\uD83D\uDC98\uD83D\uDC9D\uD83D\uDC9F\uD83D\uDD34\uD83D\uDFE0\uD83D\uDFE1\uD83D\uDFE2\uD83D\uDD35\uD83D\uDFE3⚪⚫\uD83D\uDFE4\uD83D\uDD3A\uD83D\uDD38\uD83D\uDD39\uD83D\uDD36\uD83D\uDD37\uD83D\uDFE5\uD83D\uDFE7\uD83D\uDFE8\uD83D\uDFE9\uD83D\uDFE6\uD83D\uDFEA⬛⬜\uD83D\uDFEB♠️♣️♥️♦️\uD83D\uDCA7\uD83E\uDE78⭐\uD83C\uDF1F✨\uD83D\uDCA5\uD83D\uDD25☀️"
            else "❤️\uD83E\uDDE1\uD83D\uDC9B\uD83D\uDC9A\uD83D\uDC99\uD83D\uDC9C" +
                    "\uD83D\uDDA4\uD83E\uDD0D\uD83E\uDD0E" +
        "\uD83D\uDD34\uD83D\uDFE0\uD83D\uDFE1\uD83D\uDFE2\uD83D\uDD35\uD83D\uDFE3⚪⚫\uD83D\uDFE4" +
                   "\uD83D\uDD3A" +
                    "\uD83D\uDD36\uD83D\uDD37\uD83D\uDFE5\uD83D\uDFE7\uD83D\uDFE8\uD83D\uDFE9\uD83D\uDFE6\uD83D\uDFEA⬛⬜\uD83D\uDFEB♠️♣️♥️♦️\uD83D\uDCA7\uD83E\uDE78⭐\uD83C\uDF1F✨\uD83D\uDCA5\uD83D\uDD25☀️"
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
        val rows: Int = emojiCount / COLUMNS + if (emojiCount % COLUMNS != 0) 1 else 0
        borderThickness = Utilities.convertDpToPixel(2f, context)
        borderMargin = Utilities.convertDpToPixel(10f, context)
        val halfThickness = borderThickness / 2
        emojisHorizontalGap = Utilities.convertDpToPixel(9f, context)
        emojisVerticalGap = Utilities.convertDpToPixel(9f, context)

        rcEmojisBounds = RectF(
            halfThickness,
            halfThickness,
            COLUMNS * size + emojisHorizontalGap * (COLUMNS - 1) + 2 * borderMargin - halfThickness,
            rows * size + emojisVerticalGap * (rows - 1) + 2 * borderMargin - halfThickness
        )
        val ptMainScreenSize = Utilities.getRealScreenSize(context)
        val tf = Typeface.create("Normal", Typeface.BOLD)
        paint.typeface = tf
        paint.textSize = Utilities.convertDpToPixel(25f, getContext())
        selectEmoji = resources.getString(R.string.select_emoji)
        popUpHeader = PopupHeader(selectEmoji, 0.75f*ptMainScreenSize.x)
        popUpHeader.computeData(paint)
        rcHeaderBounds = popUpHeader.rcHeaderBounds

        rcFullScreenBounds = RectF(0f, 0f, ptMainScreenSize.x.toFloat(), ptMainScreenSize.y.toFloat())

        popupMargin = Utilities.convertDpToPixel(7f, context)
        headerEmojisGap = Utilities.convertDpToPixel(30f, context)

        val innerHeight = rcHeaderBounds.height() + rcEmojisBounds.height() + headerEmojisGap
        val innerWidth = max(rcHeaderBounds.width().toFloat(), rcEmojisBounds.width())
        rcPopupBounds = RectF((ptMainScreenSize.x - innerWidth) / 2.0f - popupMargin,
            (ptMainScreenSize.y - innerHeight) / 2.0f - popupMargin,
            (ptMainScreenSize.x + innerWidth) / 2.0f + popupMargin,
            (ptMainScreenSize.y + innerHeight) / 2.0f + popupMargin)
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Constants.DARK_BACKGROUND_OPACITY
        paint.style = Paint.Style.FILL
        canvas.drawRect(rcFullScreenBounds, paint)

        paint.color = ContextCompat.getColor(context, Utilities.getBackgroundColorId(context))
        canvas.drawRect(rcPopupBounds, paint)

        paint.color = ContextCompat.getColor(context, Utilities.getTextColorId(context))
        popUpHeader.draw(canvas, paint, rcFullScreenBounds.width(),rcPopupBounds.top + popupMargin)

        canvas.translate(
            (rcFullScreenBounds.width() - rcEmojisBounds.width()) / 2.0f,
            rcPopupBounds.top + rcHeaderBounds.height() + popupMargin + headerEmojisGap
        )

        paint.color = ContextCompat.getColor(context, R.color.midDayFog)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderThickness
        canvas.drawRect(rcEmojisBounds, paint)
        canvas.translate(borderMargin, borderMargin)
        for (i in emojiCellCtrlList.indices) {
            emojiCellCtrlList[i].draw(canvas)
            if (i % COLUMNS == LAST_COLUMN) {
                canvas.translate(
                    -LAST_COLUMN * (size + emojisHorizontalGap),
                    size + emojisVerticalGap
                )
            } else {
                canvas.translate(size + emojisHorizontalGap, 0f)
            }
        }
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(rcFullScreenBounds.width().toInt(), rcFullScreenBounds.height().toInt())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event) // this super call is important !
        var success = false
        val x = event.x - (rcFullScreenBounds.width() - rcEmojisBounds.width()) / 2.0f - borderMargin
        val y = event.y - (rcPopupBounds.top + rcHeaderBounds.height() + popupMargin + headerEmojisGap) - borderMargin

        if (x >= 0 && x <= rcEmojisBounds.width() && y >= 0 && y <= rcEmojisBounds.height()) {
            val xSumSizeHorizontalGapRatio = x / (size + emojisHorizontalGap)
            val col = floor(xSumSizeHorizontalGapRatio).toInt()

            // Click is only on a button if remainder is less than size/(size + mainShapesHorizontalGap)
            if (xSumSizeHorizontalGapRatio - col < size / (size + emojisHorizontalGap)) {
                val row = floor(y / (size + emojisVerticalGap)).toInt()
                val pos = COLUMNS * row + col

                // Click on bottom border and get crash without if statement as out of range.
                // Click on right border and leftmost next item selected without COLUMNS check.
                if (pos < emojiCellCtrlList.size && col < COLUMNS && pos >= 0) {
                    selectedEmojiCtrl.setEmoji(emojiCellCtrlList[pos].emoji)
                    success = performClick()
                }
            }
        }
        return success
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onClick(v: View) {}
    fun setSelectedEmojiCtrl(selectedEmojiCtrl: EmojiCellCtrl) {
        this.selectedEmojiCtrl = selectedEmojiCtrl
        val selectedEmoji = selectedEmojiCtrl.emoji
        for (idx in emojiCellCtrlList.indices) {
            val ecc = emojiCellCtrlList[idx]
            if (ecc.emoji.compareTo(selectedEmoji) == 0) {
                ecc.isSelected = true
            }
        }
    }

    companion object {
        private var size = 0
        private const val COLUMNS = 6
        private const val LAST_COLUMN: Int = COLUMNS - 1
    }
}