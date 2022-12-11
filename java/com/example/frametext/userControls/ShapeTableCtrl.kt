package com.example.frametext.userControls

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.frametext.R
import com.example.frametext.enums.SymbolShapeType
import com.example.frametext.helpers.Utilities
import com.example.frametext.helpers.ifNotNull
import com.example.frametext.userControls.colorPicker.Constants
import kotlin.math.max

class ShapeTableCtrl : View, View.OnClickListener {
    private var columns = 0
    private var lastColumn = 0
    private var borderThickness = 0
    private var borderMargin = 0
    private lateinit var selectedShapeCtrl: ShapeCellCtrl
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private lateinit var rcShapesBounds: RectF
    private val shapeCellCtrlList = ArrayList<ShapeCellCtrl>()
    private lateinit var  selectSymbol: String
    private val rcHeaderBounds = Rect()
    private lateinit var rcFullScreenBounds: RectF
    private lateinit var rcPopupBounds: RectF
    private var popupMargin: Float = 0f
    private var headerShapesGap = 0f // Gap between header and shapes below.

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
        shapeCellCtrlList.add(ShapeCellCtrl(context, SymbolShapeType.Square, false))
        shapeCellCtrlList.add(ShapeCellCtrl(context, "▲", false))
        shapeCellCtrlList.add(ShapeCellCtrl(context, SymbolShapeType.Circle, false))
        val suitArray: Array<SymbolShapeType> = arrayOf(
            SymbolShapeType.Smiley,
            SymbolShapeType.Spade,
            SymbolShapeType.Club,
            SymbolShapeType.Heart,
            SymbolShapeType.Diamond,
            SymbolShapeType.Star
        ) // star isn't a suit but is next one we want
        for (suit in suitArray) {
            val scc = ShapeCellCtrl(context, suit, false)
            shapeCellCtrlList.add(scc)
        }
        var remainingSymbols = "✪⍟⎈❉❋✺✹✸✶✷✵✲✱✦⊛"

        if (purchasedMore) {
            remainingSymbols += "⁕❃❂✼⨳✚❖✜֎֍†‡•◙█●▬★"
        }

        for (element in remainingSymbols) {
            val scc = ShapeCellCtrl(context, element.toString(), false)
            shapeCellCtrlList.add(scc)
        }
        columns = 8
        val rows =
            shapeCellCtrlList.size / columns + if (shapeCellCtrlList.size % columns != 0) 1 else 0
        lastColumn = columns - 1
        borderThickness = Utilities.convertDpToPixel(2f, context).toInt()
        borderMargin = Utilities.convertDpToPixel(3f, context).toInt()
        val halfThickness = borderThickness / 2
        if (shapeCellCtrlList.size > 0) {
            val shapeCellCtrl = shapeCellCtrlList[0]
            size = shapeCellCtrl.size
        }
        rcShapesBounds = RectF(
            halfThickness.toFloat(),
            halfThickness.toFloat(),
            (columns * size - halfThickness + 2 * borderMargin).toFloat(),
            (rows * size - halfThickness + 2 * borderMargin).toFloat()
        )
        val ptMainScreenSize = Utilities.getRealScreenSize(context)
        val tf = Typeface.create("TimesRoman", Typeface.NORMAL)
        paint.typeface = tf
        paint.textSize = Utilities.convertDpToPixel(25f, getContext())
        selectSymbol = resources.getString(R.string.select_symbol)
        paint.getTextBounds(selectSymbol, 0, selectSymbol.length, rcHeaderBounds)
        rcFullScreenBounds = RectF(0f, 0f, ptMainScreenSize.x.toFloat(), ptMainScreenSize.y.toFloat())

        popupMargin = Utilities.convertDpToPixel(7f, context)
        headerShapesGap = Utilities.convertDpToPixel(30f, context)

        val innerHeight = rcHeaderBounds.height() + rcShapesBounds.height() + headerShapesGap
        val innerWidth = max(rcHeaderBounds.width().toFloat(), rcShapesBounds.width())
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

        paint.color = ContextCompat.getColor(context, R.color.white)
        canvas.drawRect(rcPopupBounds, paint)

        paint.color = ContextCompat.getColor(context, R.color.black)
        canvas.drawText(
            selectSymbol,
            (rcFullScreenBounds.width() - rcHeaderBounds.width()) / 2.0f,
            rcPopupBounds.top + rcHeaderBounds.height() + popupMargin,
            paint
        )
        canvas.translate(
            (rcFullScreenBounds.width() - rcShapesBounds.width()) / 2.0f,
            rcPopupBounds.top + rcHeaderBounds.height() + popupMargin + headerShapesGap
        )
        paint.color = ContextCompat.getColor(context, R.color.midDayFog)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderThickness.toFloat()
        canvas.drawRect(rcShapesBounds, paint)
        canvas.translate(borderMargin.toFloat(), borderMargin.toFloat())
        for (i in shapeCellCtrlList.indices) {
            shapeCellCtrlList[i].draw(canvas)
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
        setMeasuredDimension(rcFullScreenBounds.width().toInt(), rcFullScreenBounds.height().toInt())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event) // this super call is important !!!
        var success = false
        val x = event.x - (rcFullScreenBounds.width() - rcShapesBounds.width()) / 2.0f
        val y = event.y - (rcPopupBounds.top + rcHeaderBounds.height() + popupMargin + headerShapesGap)
        val col: Int = (x - borderMargin).toInt() / size
        val row: Int = (y - borderMargin).toInt() / size
        val pos = columns * row + col

        // Click on bottom border and get crash without if statement as out of range.
        // Click on right border and leftmost next item selected without columns check.
        if (pos < shapeCellCtrlList.size && col < columns && pos >= 0) {
            val sCC = shapeCellCtrlList[pos]
            if (sCC.getShapeType() !== SymbolShapeType.None) {
                selectedShapeCtrl.setShapeType(sCC.getShapeType())
            } else {
                selectedShapeCtrl.setSymbol(sCC.getSymbol())
            }
            success = performClick()
        }
        return success
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onClick(v: View) {}
    fun setSelectedShapeCtrl(selectedShapeCtrl: ShapeCellCtrl) {
        this.selectedShapeCtrl = selectedShapeCtrl
        for (idx in shapeCellCtrlList.indices) {
            val scc = shapeCellCtrlList[idx]
            if (selectedShapeCtrl.getShapeType() !== SymbolShapeType.None) {
                if (scc.getShapeType() === selectedShapeCtrl.getShapeType()) {
                    scc.isSelected = true
                }
            } else ifNotNull(selectedShapeCtrl.getSymbol(), scc.getSymbol()) {
                selectedShapeCtrlSymbol, symbol ->
                run {
                    if ((selectedShapeCtrlSymbol).compareTo(symbol) == 0) {
                        scc.isSelected = true
                    }
                }
            }
        }
    }

    companion object {
        private var size = 0
    }
}
