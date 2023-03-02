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
import com.example.frametext.userControls.colorPicker.Constants
import kotlin.math.floor
import kotlin.math.max

class MainShapeTableCtrl : View, View.OnClickListener {
    private val columns: Int
    private val lastColumn: Int
    private lateinit var selectedMainShapeCtrl: MainShapeCellCtrl
    private val paint = Paint()
    // mainShapesBoundingRect is the bounding rect enclosing all main shapes
    private lateinit var rcMainShapesBounds: RectF
    private val mainShapeCellCtrlList = ArrayList<MainShapeCellCtrl>()
    private lateinit var selectMainShape: String
    private lateinit var rcHeaderBounds: Rect
    private lateinit var rcFullScreenBounds: RectF
    private lateinit var rcPopupBounds: RectF
    private var popupMargin: Float = 0f
    private var headerMainShapesGap = 0f // Gap between header and main shapes below.
    private var mainShapesHorizontalGap = 0f // Gap between the main shapes
    private lateinit var popUpHeader: PopupHeader

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
        selectedMainShapeCtrl: MainShapeCellCtrl
    ) : super(context) {
        columns = 4
        lastColumn = columns - 1
        this.selectedMainShapeCtrl = selectedMainShapeCtrl

        for (shapeType in shapeTypes) {
            val mSCC = MainShapeCellCtrl(context, shapeType, showBorder = false)

            if (mSCC.getShapeType() === selectedMainShapeCtrl.getShapeType()) {
                mSCC.isSelected = true
            }

            mainShapeCellCtrlList.add(mSCC)
        }
        val rows = shapeTypes.size / columns + if (shapeTypes.size % columns != 0) 1 else 0
        if (mainShapeCellCtrlList.size > 0) {
            val mainShapeCellCtrl = mainShapeCellCtrlList[0]
            size = mainShapeCellCtrl.size
        }

        val ptMainScreenSize = Utilities.getRealScreenSize(context)
        val tf = Typeface.create("Normal", Typeface.BOLD)
        paint.typeface = tf
        paint.textSize = Utilities.convertDpToPixel(25f, getContext())
        selectMainShape = resources.getString(R.string.select_main_shape)

        popUpHeader = PopupHeader(selectMainShape, 0.75f*ptMainScreenSize.x)
        popUpHeader.computeData(paint)
        rcHeaderBounds = popUpHeader.rcHeaderBounds

        rcFullScreenBounds = RectF(0f, 0f, ptMainScreenSize.x.toFloat(), ptMainScreenSize.y.toFloat())

        popupMargin = Utilities.convertDpToPixel(7f, context)
        headerMainShapesGap = Utilities.convertDpToPixel(25f, context)
        mainShapesHorizontalGap = Utilities.convertDpToPixel(12f, context)
        // Might need a vertical gap in future

        rcMainShapesBounds = RectF(
            0f,
            0f,
            (columns * size).toFloat() + mainShapesHorizontalGap*(columns - 1),
            (rows * size).toFloat()
        )

        val innerHeight = rcHeaderBounds.height() + rcMainShapesBounds.height() + headerMainShapesGap
        // check which wider: header or main shapes?
        val widest = max(rcHeaderBounds.width().toFloat(), rcMainShapesBounds.width())

        rcPopupBounds = RectF((ptMainScreenSize.x - widest) / 2.0f - popupMargin,
            (ptMainScreenSize.y - innerHeight) / 2.0f - popupMargin,
            (ptMainScreenSize.x + widest) / 2.0f + popupMargin,
            (ptMainScreenSize.y + innerHeight) / 2.0f
                    + popupMargin
        )
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
            (rcFullScreenBounds.width() - rcMainShapesBounds.width()) / 2.0f,
            rcPopupBounds.top + rcHeaderBounds.height() + popupMargin + headerMainShapesGap
        )
        paint.color = ContextCompat.getColor(context, R.color.midDayFog)
        paint.style = Paint.Style.STROKE
        for (i in mainShapeCellCtrlList.indices) {
            mainShapeCellCtrlList[i].draw(canvas)
            if (i % columns == lastColumn) {
                canvas.translate(-lastColumn * (size + mainShapesHorizontalGap), size.toFloat())
            } else {
                canvas.translate(size + mainShapesHorizontalGap, 0f)
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
        val x = event.x - (rcFullScreenBounds.width() - rcMainShapesBounds.width()) / 2.0f
        val y = event.y - (rcPopupBounds.top + popupMargin + rcHeaderBounds.height() + headerMainShapesGap)

        if (x >= 0 && x <= rcMainShapesBounds.width() && y >= 0 && y <= rcMainShapesBounds.height()) {
            val xSumSizeHorizontalGapRatio = x / (size + mainShapesHorizontalGap)
            val col = floor(xSumSizeHorizontalGapRatio).toInt()

            // Click is only on a button if remainder is less than size/(size + mainShapesHorizontalGap)
            if (xSumSizeHorizontalGapRatio - col < size/(size + mainShapesHorizontalGap)) {
                val row = y.toInt() / size
                val pos = columns * row + col
                // Click on bottom border and get crash without if statement as out of range.
                // Click on right border and leftmost next item selected without columns check.
                if (pos < mainShapeCellCtrlList.size && col < columns && pos >= 0) {
                    selectedMainShapeCtrl.setShapeType(mainShapeCellCtrlList[pos].getShapeType())
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

    companion object {
        private var size = 0
    }
}
