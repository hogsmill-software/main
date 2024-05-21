package com.example.frametext.userControls.colorPicker

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.frametext.R
import kotlin.math.min


class ColorPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr), ColorObservable {
    private val colorWheelView: ColorWheelView
    private var brightnessSliderView: BrightnessSliderView? = null
    private var alphaSliderView: AlphaSliderView? = null
    private var observableOnDuty: ColorObservable? = null
    private var onlyUpdateOnTouchEventUp: Boolean
    private var initialColor = Color.BLACK
    private val sliderMargin: Int
    private val sliderHeight: Int
    fun setOnlyUpdateOnTouchEventUp(onlyUpdateOnTouchEventUp: Boolean) {
        this.onlyUpdateOnTouchEventUp = onlyUpdateOnTouchEventUp
        updateObservableOnDuty()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)
        val maxHeight = MeasureSpec.getSize(heightMeasureSpec)
        var desiredWidth = maxHeight - (paddingTop + paddingBottom) + (paddingLeft + paddingRight)
        brightnessSliderView?.let {
            desiredWidth -= sliderMargin + sliderHeight
        }
        alphaSliderView?.let {
            desiredWidth -= sliderMargin + sliderHeight
        }
        val width = min(maxWidth, desiredWidth)
        var height = width - (paddingLeft + paddingRight) + (paddingTop + paddingBottom)
        brightnessSliderView?.let {
            height += sliderMargin + sliderHeight
        }
        alphaSliderView?.let {
            height += sliderMargin + sliderHeight
        }
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.getMode(widthMeasureSpec)),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(heightMeasureSpec))
        )
    }

    fun setInitialColor(color: Int) {
        initialColor = color
        colorWheelView.setColor(color, true)
    }

    fun setEnabledBrightness(enable: Boolean) {
        if (enable) {
            if (brightnessSliderView == null) {
                brightnessSliderView = BrightnessSliderView(context)
                val params = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, sliderHeight)
                params.topMargin = sliderMargin
                addView(brightnessSliderView, 1, params)
            }
            brightnessSliderView?.let { it.bind(colorWheelView) }
        } else {
            brightnessSliderView?.let {
                it.unbind()
                removeView(it)
                brightnessSliderView = null
            }
        }
        updateObservableOnDuty()
        alphaSliderView?.let {
            setEnabledAlpha(true)
        }
    }

    fun setEnabledAlpha(enable: Boolean) {
        if (enable) {
            if (alphaSliderView == null) {
                alphaSliderView = AlphaSliderView(context)
                val params = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, sliderHeight)
                params.topMargin = sliderMargin
                addView(alphaSliderView, params)
            }
            var bindTo: ColorObservable? = brightnessSliderView
            if (bindTo == null) {
                bindTo = colorWheelView
            }
            alphaSliderView?.let { it.bind(bindTo) }
        } else {
            alphaSliderView?.let {
                it.unbind()
                removeView(it)
                alphaSliderView = null
            }
        }
        updateObservableOnDuty()
    }

    private fun updateObservableOnDuty() {
        observableOnDuty?.let { observableOnDutyIt ->
            observers?.let { observersIt ->
                for (observer in observersIt) {
                    observableOnDutyIt.unsubscribe(observer)
                }
            }
        }
        colorWheelView.setOnlyUpdateOnTouchEventUp(false)
        brightnessSliderView?.let {
            it.setOnlyUpdateOnTouchEventUp(false)
        }
        alphaSliderView?.let {
            it.setOnlyUpdateOnTouchEventUp(false)
        }
        if (brightnessSliderView == null && alphaSliderView == null) {
            observableOnDuty = colorWheelView
            colorWheelView.setOnlyUpdateOnTouchEventUp(onlyUpdateOnTouchEventUp)
        } else {
            alphaSliderView?.let {
                observableOnDuty = alphaSliderView
                it.setOnlyUpdateOnTouchEventUp(onlyUpdateOnTouchEventUp)
            } ?: run {
                observableOnDuty = brightnessSliderView
                brightnessSliderView?.let {
                    it.setOnlyUpdateOnTouchEventUp(onlyUpdateOnTouchEventUp)
                }
            }
        }
        observers?.let {
            for (observer in it) {
                observableOnDuty?.let {
                    it.subscribe(observer as ColorPickerPopup.ColorPickerObserver)
                    observer.onColor(
                        color = it.color,
                        fromUser = false,
                        shouldPropagate = true
                    )
                }
            }
        }
    }

    fun reset() {
        colorWheelView.setColor(initialColor, true)
    }

    private var observers: MutableList<ColorObserver>? = ArrayList()
    override fun subscribe(observer: ColorObserver) {
        observableOnDuty?.let {
            it.subscribe(observer)
        }
        observers?.let {
            it.add(observer)
        }
    }

    override fun unsubscribe(observer: ColorObserver?) {
        observableOnDuty?.let {
            it.unsubscribe(observer)
        }
        observers?.let {
            it.remove(observer)
        }
    }

    override val color: Int
        get() = observableOnDuty?.let { it.color } ?: 0

    init {
        orientation = VERTICAL
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerView)
        val enableAlpha = typedArray.getBoolean(R.styleable.ColorPickerView_enableAlpha, false)
        val enableBrightness =
            typedArray.getBoolean(R.styleable.ColorPickerView_enableBrightness, true)
        onlyUpdateOnTouchEventUp =
            typedArray.getBoolean(R.styleable.ColorPickerView_onlyUpdateOnTouchEventUp, false)
        typedArray.recycle()
        colorWheelView = ColorWheelView(context)
        val density = resources.displayMetrics.density
        val margin = (8 * density).toInt()
        sliderMargin = 2 * margin
        sliderHeight = (24 * density).toInt()
        val params = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        addView(colorWheelView, params)
        setEnabledBrightness(enableBrightness)
        setEnabledAlpha(enableAlpha)
        setPadding(margin, margin, margin, margin)
    }
}


