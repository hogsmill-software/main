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
import kotlin.math.abs

class ShapeTableCtrl : View, View.OnClickListener {
    private var columns = 0
    private var lastColumn = 0
    private var borderThickness = 0
    private var borderMargin = 0
    private var selectedShapeCtrl: ShapeCellCtrl? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var boundingRect: RectF? = null
    private val shapeCellCtrlList = ArrayList<ShapeCellCtrl>()
    private var popUpSize: Point? = null
    private var selectSymbol: String? = null
    private val rcBounds = Rect()
    private var verticalGapHeight = 0f
    private var popUpBound: RectF? = null

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
        boundingRect = RectF(
            halfThickness.toFloat(),
            (rows * size - halfThickness + 2 * borderMargin).toFloat(),
            (columns * size - halfThickness + 2 * borderMargin).toFloat(),
            halfThickness.toFloat()
        )
        popUpSize = Utilities.getRealScreenSize(context)
        val tf = Typeface.create("TimesRoman", Typeface.NORMAL)
        paint.typeface = tf
        paint.textSize = Utilities.convertDpToPixel(40f, getContext())
        selectSymbol = resources.getString(R.string.select_symbol)
        paint.getTextBounds(selectSymbol, 0, selectSymbol!!.length, rcBounds)
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
            selectSymbol!!,
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
        paint.strokeWidth = borderThickness.toFloat()
        canvas.drawRect(boundingRect!!, paint)
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
        setMeasuredDimension(popUpSize!!.x, popUpSize!!.y)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event) // this super call is important !!!
        var success = false
        val x = event.x - (popUpSize!!.x - boundingRect!!.width()) / 2.0f
        val y = event.y - (2 * verticalGapHeight + rcBounds.height())
        val col: Int = (x - borderMargin).toInt() / size
        val row: Int = (y - borderMargin).toInt() / size
        val pos = columns * row + col

        // Click on bottom border and get crash without if statement as out of range.
        // Click on right border and leftmost next item selected without columns check.
        if (pos < shapeCellCtrlList.size && col < columns && pos >= 0) {
            val sCC = shapeCellCtrlList[pos]
            if (sCC.getShapeType() !== SymbolShapeType.None) {
                selectedShapeCtrl!!.setShapeType(sCC.getShapeType())
            } else {
                selectedShapeCtrl!!.setSymbol(sCC.getSymbol())
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
            } else if (selectedShapeCtrl.getSymbol() != null && scc.getSymbol() != null) {
                if (scc.getSymbol()!!.compareTo(selectedShapeCtrl.getSymbol()!!) == 0) {
                    scc.isSelected = true
                }
            }
        }
    }

    companion object {
        private var size = 0
    }
}
