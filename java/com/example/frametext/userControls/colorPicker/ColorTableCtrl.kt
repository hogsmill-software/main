package com.example.frametext.userControls.colorPicker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.frametext.R
import kotlin.math.floor

class ColorTableCtrl : View, View.OnClickListener, ColorObservable {
    private val colorCellCtrlList = ArrayList<ColorCellCtrl>()
    override var color: Int = 0
        private set
    private val emitter = ColorObservableEmitter()
    var colorPickerPopup: ColorPickerPopup? = null

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
        var colorCount = 0
        val colorList = arrayOf(R.color.black, R.color.white, R.color.red, R.color.green, R.color.blue, R.color.yellow, R.color.magenta, R.color.cyan,
            R.color.lightGrey, R.color.darkGrey, R.color.darkRed, R.color.dirtyRed, R.color.orange, R.color.beige, R.color.gold, R.color.flameYellow,
            R.color.lime, R.color.lightGreen, R.color.aqua, R.color.turquoise, R.color.indigo, R.color.purplePink, R.color.rose, R.color.brown,
            R.color.purple_200, R.color.purple_500, R.color.purple_700, R.color.teal_200, R.color.teal_700, R.color.pinkMagenta, R.color.navyBlue, R.color.fog,
            R.color.midDayFog, R.color.plainBlue, R.color.highlightBlue, R.color.faintHighlightBlue, R.color.britishRacingGreen, R.color.bleuDeFrance, R.color.rossoCorsa, R.color.ghostWhite,
            R.color.purple, R.color.darkGreen, R.color.olive, R.color.deepPink, R.color.unmellowYellow, R.color.darkKhaki, R.color.orangeRed, R.color.crimson)

        while (colorCount < colorList.size) {
            colorCellCtrlList.add(ColorCellCtrl(context, colorList[colorCount]))
            colorCount++
        }
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        // Need to subtract frame width or right frames of right cells are not drawn.
        var drawableWidth = width - ColorCellCtrl.selectFrameWidth
        for (row in 0..lastRow) {
            canvas.save()
            for (column in 0..lastColumn) {
                colorCellCtrlList[row*columns + column].draw(canvas)
                // Need to add 1 as drawn AFTER width of ColorCellCtrl - otherwise overlay 1 pixel.
                val horizontalTranslate = ColorCellCtrl.size + (drawableWidth - columns*ColorCellCtrl.size)/columns + 1
                canvas.translate(horizontalTranslate, 0f)
            }
            canvas.restore()
            canvas.translate(0f, ColorCellCtrl.size)
        }
        canvas.restore()
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, rows*ColorCellCtrl.size.toInt())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event) // this super call is important !!!
        var success = false
        val gapWidth = (width - columns*ColorCellCtrl.size)/columns + 1
        val colPos = event.x/(ColorCellCtrl.size + gapWidth)
        val col: Int = floor(colPos).toInt()

        // Check didn't click on gap
        if (colPos - col < ColorCellCtrl.size/(ColorCellCtrl.size + gapWidth)) {
            colorCellCtrlList.forEach {it.isSelected = false}
            val row: Int = (event.y/ColorCellCtrl.size).toInt()
            val pos = row*columns + col

            if (pos < colorCellCtrlList.size && pos >= 0) {
                val activeCellCtrl = colorCellCtrlList[pos]
                activeCellCtrl.isSelected = true
                color = activeCellCtrl.color
                // doesn't work.
                emitter.onColor(color = color, fromUser = true, shouldPropagate = true)
                this.colorPickerPopup?.setColorFromColorTableCtrlView(color)
                this.invalidate()
                success = performClick()
            }
        }

        return success
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onClick(v: View) {}

    override fun subscribe(observer: ColorObserver) {
        emitter.subscribe(observer)
    }

    override fun unsubscribe(observer: ColorObserver?) {
        emitter.unsubscribe(observer)
    }

    fun setColor(color: Int) {
        colorCellCtrlList.forEach { it.isSelected = (it.color == color) }
    }

    companion object {
        private const val columns = 8
        private const val lastColumn: Int = columns - 1
        private const val rows = 6
        private const val lastRow: Int = rows - 1
    }
}