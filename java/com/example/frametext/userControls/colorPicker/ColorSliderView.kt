package com.example.frametext.userControls.colorPicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

abstract class ColorSliderView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr), ColorObservable, Updatable {
    protected var baseColor = Color.WHITE
    private val colorPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val selectorPaint: Paint
    private val selectorPath: Path
    private val currentSelectorPath = Path()
    protected var selectorSize = 0f
    protected var currentValue = 1f
    private var onlyUpdateOnTouchEventUp = false
    private val emitter: ColorObservableEmitter = ColorObservableEmitter()
   // private val handler: ThrottledTouchEventHandler = ThrottledTouchEventHandler(this)
    private val handler: ThrottledTouchEventHandler by lazy { ThrottledTouchEventHandler(this) }
    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        configurePaint(colorPaint)
        selectorPath.reset()
        selectorSize = h * 0.25f
        selectorPath.moveTo(0f, 0f)
        selectorPath.lineTo(selectorSize * 2, 0f)
        selectorPath.lineTo(selectorSize, selectorSize)
        selectorPath.close()
    }

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        canvas.drawRect(selectorSize, selectorSize, width - selectorSize, height, colorPaint)
        canvas.drawRect(selectorSize, selectorSize, width - selectorSize, height, borderPaint)
        selectorPath.offset(currentValue * (width - 2 * selectorSize), 0f, currentSelectorPath)
        canvas.drawPath(currentSelectorPath, selectorPaint)
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
            updateValue(event.x)

            val isTouchUpEvent = event.actionMasked == MotionEvent.ACTION_UP
            if (!onlyUpdateOnTouchEventUp || isTouchUpEvent) {
                emitter.onColor(assembleColor(), true, isTouchUpEvent)
            }
        }
    }

    fun setBaseColor(color: Int, fromUser: Boolean, shouldPropagate: Boolean) {
        baseColor = color
        configurePaint(colorPaint)
        var targetColor = color
        if (!fromUser) {
            // if not set by user (means programmatically), resolve currentValue from color value
            currentValue = resolveValue(color)
        } else {
            targetColor = assembleColor()
        }
        if (!onlyUpdateOnTouchEventUp) {
            emitter.onColor(targetColor, fromUser, shouldPropagate)
        } else if (shouldPropagate) {
            emitter.onColor(targetColor, fromUser, true)
        }
        invalidate()
    }

    private fun updateValue(eventX: Float) {
        var eventXTemp = eventX
        val left = selectorSize
        val right = width - selectorSize
        if (eventXTemp < left) eventXTemp = left
        if (eventXTemp > right) eventXTemp = right
        currentValue = (eventXTemp - left) / (right - left)
        invalidate()
    }

    protected abstract fun resolveValue(color: Int): Float
    protected abstract fun configurePaint(colorPaint: Paint?)
    protected abstract fun assembleColor(): Int
    override fun subscribe(observer: ColorObserver) {
        emitter.subscribe(observer)
    }

    override fun unsubscribe(observer: ColorObserver?) {
        emitter.unsubscribe(observer)
    }

    override val color: Int
        get() = emitter.color

    fun setOnlyUpdateOnTouchEventUp(onlyUpdateOnTouchEventUp: Boolean) {
        this.onlyUpdateOnTouchEventUp = onlyUpdateOnTouchEventUp
    }

    private val bindObserver: ColorObserver =
        object : ColorObserver {
            override fun onColor(color: Int, fromUser: Boolean, shouldPropagate: Boolean) {
                setBaseColor(
                    color,
                    fromUser,
                    shouldPropagate
                )
            }
        }
    private var boundObservable: ColorObservable? = null
    fun bind(colorObservable: ColorObservable?) {
        if (colorObservable != null) {
            colorObservable.subscribe(bindObserver)
            setBaseColor(colorObservable.color, fromUser = true, shouldPropagate = true)
        }
        boundObservable = colorObservable
    }

    fun unbind() {
        if (boundObservable != null) {
            boundObservable!!.unsubscribe(bindObserver)
            boundObservable = null
        }
    }

    init {
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 0f
        borderPaint.color = Color.BLACK
        selectorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        selectorPaint.color = Color.BLACK
        selectorPath = Path()
        selectorPath.fillType = Path.FillType.WINDING
    }
}

