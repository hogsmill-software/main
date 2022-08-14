package com.example.frametext.userControls

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.frametext.R
import com.example.frametext.enums.MainShapeType
import com.example.frametext.helpers.Utilities
import kotlin.math.abs

class MainShapeTableCtrl : View, View.OnClickListener {
    private val columns: Int
    private val lastColumn: Int
    private var borderThickness = 0
    private var borderMargin = 0
    private var selectedMainShapeCtrl: MainShapeCellCtrl? = null
    private val paint = Paint()
    private var boundingRect: RectF? = null
    private val mainShapeCellCtrlList = ArrayList<MainShapeCellCtrl>()
    private var fillShape = true
    private var popUpSize: Point? = null
    private var selectMainShape: String? = null
    private val rcBounds = Rect()
    private var verticalGapHeight = 0f
    private var popUpBound: RectF? = null

    constructor(context: Context?) : super(context) {
        columns = 0
        lastColumn = -1
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        columns = 0
        lastColumn = -1
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        columns = 0
        lastColumn = -1
    }

    constructor(
        context: Context,
        shapeTypes: Array<MainShapeType>,
        fillShape: Boolean
    ) : super(context) {
        columns = 3
        lastColumn = columns - 1
        this.fillShape = fillShape
        init(context, shapeTypes)
    }

    private fun init(context: Context, shapeTypes: Array<MainShapeType>) {
        for (shapeType in shapeTypes) {
            val mSCC = MainShapeCellCtrl(context, shapeType, fillShape, false)
            mainShapeCellCtrlList.add(mSCC)
        }
        val rows = shapeTypes.size / columns + if (shapeTypes.size % columns != 0) 1 else 0
        borderThickness = Utilities.convertDpToPixel(2f, context).toInt()
        borderMargin = Utilities.convertDpToPixel(3f, context).toInt()
        val halfThickness = borderThickness / 2
        if (mainShapeCellCtrlList.size > 0) {
            val mainShapeCellCtrl = mainShapeCellCtrlList[0]
            size = mainShapeCellCtrl.size
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
        selectMainShape = resources.getString(R.string.select_main_shape)
        paint.getTextBounds(selectMainShape, 0, selectMainShape!!.length, rcBounds)
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
            selectMainShape!!,
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
        for (i in mainShapeCellCtrlList.indices) {
            mainShapeCellCtrlList[i].draw(canvas)
            if (i % columns == lastColumn) {
                canvas.translate((-lastColumn * size).toFloat(), size.toFloat())
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
        val col = (x - borderMargin).toInt() / size
        val row = (y - borderMargin).toInt() / size
        val pos = columns * row + col

        // Click on bottom border and get crash without if statement as out of range.
        // Click on right border and leftmost next item selected without columns check.
        if (pos < mainShapeCellCtrlList.size && col < columns && pos >= 0) {
            selectedMainShapeCtrl!!.setShapeType(mainShapeCellCtrlList[pos].getShapeType())
            success = performClick()
        }
        return success
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onClick(v: View) {}
    fun setSelectedEmojiCtrl(selectedMainShapeCtrl: MainShapeCellCtrl) {
        this.selectedMainShapeCtrl = selectedMainShapeCtrl
        for (idx in mainShapeCellCtrlList.indices) {
            val mSCC = mainShapeCellCtrlList[idx]
            if (mSCC.getShapeType() === selectedMainShapeCtrl.getShapeType()) {
                mSCC.isSelected = true
            }
        }
    }

    companion object {
        private var size = 0
    }
}
