package com.example.frametext.userControls.colorPicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import kotlin.math.*

class ColorWheelView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context!!, attrs, defStyleAttr), ColorObservable, Updatable {
    private var radius = 0f
    private var centerX = 0f
    private var centerY = 0f
    private val selectorRadiusPx: Float = Constants.SELECTOR_RADIUS_DP * resources.displayMetrics.density
    private val currentPoint = PointF()
    private var currentColor = Color.MAGENTA
    private var onlyUpdateOnTouchEventUp = false
    private var selector: ColorWheelSelector? = null
    private val emitter = ColorObservableEmitter()
    private val handler = ThrottledTouchEventHandler(this)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)
        val maxHeight = MeasureSpec.getSize(heightMeasureSpec)
        val width: Int
        val height: Int = min(maxWidth, maxHeight)
        width = height
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        val netWidth = w - paddingLeft - paddingRight
        val netHeight = h - paddingTop - paddingBottom
        radius = min(netWidth, netHeight) * 0.5f - selectorRadiusPx
        if (radius < 0) return
        centerX = netWidth * 0.5f
        centerY = netHeight * 0.5f
        setColor(currentColor, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                handler.onTouchEvent(event)
                return true
            }
            MotionEvent.ACTION_UP -> {
                update(event)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun update(event: MotionEvent?) {
        if (event != null) {
            val x = event.x
            val y = event.y
            val isTouchUpEvent = event.actionMasked == MotionEvent.ACTION_UP
            if (!onlyUpdateOnTouchEventUp || isTouchUpEvent) {
                emitter.onColor(getColorAtPoint(x, y), true, isTouchUpEvent)
            }
            updateSelector(x, y)
        }
    }

    private fun getColorAtPoint(eventX: Float, eventY: Float): Int {
        val x = eventX - centerX
        val y = eventY - centerY
        val r = sqrt((x * x + y * y).toDouble())
        val hsv = floatArrayOf(0f, 0f, 1f)
        hsv[0] = (atan2(y.toDouble(), -x.toDouble()) / Math.PI * 180f).toFloat() + 180
        hsv[1] = max(0f, min(1f, (r / radius).toFloat()))
        return Color.HSVToColor(hsv)
    }

    fun setOnlyUpdateOnTouchEventUp(onlyUpdateOnTouchEventUp: Boolean) {
        this.onlyUpdateOnTouchEventUp = onlyUpdateOnTouchEventUp
    }

    fun setColor(color: Int, shouldPropagate: Boolean) {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        val r = hsv[1] * radius
        val radian = (hsv[0] / 180f * Math.PI).toFloat()
        updateSelector(
            (r * cos(radian.toDouble()) + centerX).toFloat(),
            (-r * sin(radian.toDouble()) + centerY).toFloat()
        )
        currentColor = color
        if (!onlyUpdateOnTouchEventUp) {
            emitter.onColor(color, false, shouldPropagate)
        }
    }

    private fun updateSelector(eventX: Float, eventY: Float) {
        var x = eventX - centerX
        var y = eventY - centerY
        val r = sqrt((x * x + y * y).toDouble())
        if (r > radius) {
            x *= (radius / r).toFloat()
            y *= (radius / r).toFloat()
        }
        currentPoint.x = x + centerX
        currentPoint.y = y + centerY
        selector?.setCurrentPoint(currentPoint)
    }

    override fun subscribe(observer: ColorObserver) {
        emitter.subscribe(observer)
    }

    override fun unsubscribe(observer: ColorObserver?) {
        emitter.unsubscribe(observer)
    }

    override val color: Int
        get() = emitter.color

    init {
        run {
            val layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            val palette = ColorWheelPalette(context)
            val padding = selectorRadiusPx.toInt()
            palette.setPadding(padding, padding, padding, padding)
            addView(palette, layoutParams)
        }
        run {
            val layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            selector = ColorWheelSelector(context)
            selector!!.setSelectorRadiusPx(selectorRadiusPx)
            addView(selector, layoutParams)
        }
    }
}
