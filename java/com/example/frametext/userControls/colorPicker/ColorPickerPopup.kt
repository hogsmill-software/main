package com.example.frametext.userControls.colorPicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.*
import android.view.View.OnTouchListener
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.frametext.R
import java.util.*

class ColorPickerPopup private constructor(builder: Builder) {
    private val context: Context
    private var popupWindow: PopupWindow? = null
    private var popupWindowDarkBackground: PopupWindow? = null
    private val initialColor: Int
    private val enableBrightness: Boolean
    private val enableAlpha: Boolean
    private val okTitle: String
    private val cancelTitle: String
    private val showIndicator: Boolean
    private val showValue: Boolean
    private val onlyUpdateOnTouchEventUp: Boolean
    fun show(observer: ColorPickerObserver?) {
        if (observer != null) {
            show(null, observer)
        }
    }

    fun show(parent: View?, observer: ColorPickerObserver) {
        var tempParent = parent
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        @SuppressLint("InflateParams") val layout: View =
            inflater.inflate(R.layout.color_picker_popup, null)
        val colorPickerView: ColorPickerView = layout.findViewById(R.id.colorPickerView)
        popupWindow = PopupWindow(
            layout, ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        popupWindow!!.isOutsideTouchable = false // so doesn't close if click on outside
        val blankLayout: View = inflater.inflate(R.layout.blank_screen_popup, null)
        popupWindowDarkBackground = PopupWindow(
            blankLayout, ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        popupWindowDarkBackground!!.setBackgroundDrawable(ColorDrawable(0x66000000))
        popupWindowDarkBackground!!.isOutsideTouchable =
            false // so doesn't close if click on outside
        colorPickerView.setInitialColor(initialColor)
        colorPickerView.setEnabledBrightness(enableBrightness)
        colorPickerView.setEnabledAlpha(enableAlpha)
        colorPickerView.setOnlyUpdateOnTouchEventUp(onlyUpdateOnTouchEventUp)
        colorPickerView.subscribe(observer)
        val cancel = layout.findViewById<TextView>(R.id.cancel)
        cancel.text = cancelTitle
        cancel.setOnClickListener {
            popupWindowDarkBackground!!.dismiss()
            popupWindow!!.dismiss()
        }
        val ok = layout.findViewById<TextView>(R.id.ok)
        ok.text = okTitle
        ok.setOnClickListener {
            popupWindowDarkBackground!!.dismiss()
            popupWindow!!.dismiss()
            observer.onColorPicked(colorPickerView.color)
        }
        val colorIndicator = layout.findViewById<View>(R.id.colorIndicator)
        val colorHex = layout.findViewById<TextView>(R.id.colorHex)
        colorIndicator.visibility = if (showIndicator) View.VISIBLE else View.GONE
        colorHex.visibility = if (showValue) View.VISIBLE else View.GONE
        if (showIndicator) {
            colorIndicator.setBackgroundColor(initialColor)
        }
        if (showValue) {
            colorHex.text = colorHex(initialColor)
        }

        val observer2 = object : ColorObserver {
            override fun onColor(color: Int, fromUser: Boolean, shouldPropagate: Boolean) {
                if (showIndicator) {
                    colorIndicator.setBackgroundColor(color)
                }
                if (showValue) {
                    colorHex.text = colorHex(color)
                }
            }
        }
        colorPickerView.subscribe(observer2)

        // My code additions
        val customBtn = layout.findViewById<AppCompatButton>(R.id.custom)
        customBtn.setOnClickListener {
            setCustomTab(
                layout
            )
        }
        val standardBtn = layout.findViewById<AppCompatButton>(R.id.standard)
        standardBtn.setOnClickListener {
            setStandardTab(layout)
        }
        val spinnerControls = layout.findViewById<LinearLayout>(R.id.spinnerControls)
        spinnerControls.visibility = View.GONE
        initializeSpinnerControls(layout)
        initializeColorButtons(layout)
        popupWindow!!.animationStyle = R.style.TopDefaultsViewColorPickerPopupAnimation
        if (tempParent == null) tempParent = layout
        popupWindowDarkBackground!!.showAtLocation(tempParent, Gravity.NO_GRAVITY, 0, 0)
        popupWindow!!.showAtLocation(tempParent, Gravity.CENTER, 0, 0)
    }

    class Builder(val context: Context) {
        var initialColor = Color.MAGENTA
        var enableBrightness = true
        var enableAlpha = false
        var okTitle = "OK"
        var cancelTitle = "Cancel"
        var showIndicator = true
        var showValue = true
        val onlyUpdateOnTouchEventUp = false
        fun initialColor(color: Int): Builder {
            initialColor = color
            return this
        }

        fun enableBrightness(enable: Boolean): Builder {
            enableBrightness = enable
            return this
        }

        fun enableAlpha(enable: Boolean): Builder {
            enableAlpha = enable
            return this
        }

        fun okTitle(title: String): Builder {
            okTitle = title
            return this
        }

        fun cancelTitle(title: String): Builder {
            cancelTitle = title
            return this
        }

        fun showIndicator(show: Boolean): Builder {
            showIndicator = show
            return this
        }

        fun showValue(show: Boolean): Builder {
            showValue = show
            return this
        }

        fun build(): ColorPickerPopup {
            return ColorPickerPopup(this)
        }
    }

    private fun colorHex(color: Int): String {
        val a = Color.alpha(color)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return String.format(Locale.getDefault(), "0x%02X%02X%02X%02X", a, r, g, b)
    }

    private fun initializeSpinnerControls(layout: View) {
        val incrementOpacityButton = layout.findViewById<ImageButton>(R.id.incrementOpacityButton)
        val decrementOpacityButton = layout.findViewById<ImageButton>(R.id.decrementOpacityButton)
        val hexNumberOpacity = layout.findViewById<TextView>(R.id.hexNumberOpacity)
        incrementOpacityButton.setOnClickListener {
            onUpClick(
                hexNumberOpacity,
                layout,
                1
            )
        }
        decrementOpacityButton.setOnClickListener {
            onDownClick(
                hexNumberOpacity,
                layout,
                1
            )
        }
        repeatTouchListenersInitialize(incrementOpacityButton, hexNumberOpacity, layout, 1, true)
        repeatTouchListenersInitialize(decrementOpacityButton, hexNumberOpacity, layout, 1, false)
        val incrementRedButton = layout.findViewById<ImageButton>(R.id.incrementRedButton)
        val decrementRedButton = layout.findViewById<ImageButton>(R.id.decrementRedButton)
        val hexNumberRed = layout.findViewById<TextView>(R.id.hexNumberRed)
        incrementRedButton.setOnClickListener {
            onUpClick(
                hexNumberRed,
                layout,
                2
            )
        }
        decrementRedButton.setOnClickListener {
            onDownClick(
                hexNumberRed,
                layout,
                2
            )
        }
        repeatTouchListenersInitialize(incrementRedButton, hexNumberRed, layout, 2, true)
        repeatTouchListenersInitialize(decrementRedButton, hexNumberRed, layout, 2, false)
        val incrementGreenButton = layout.findViewById<ImageButton>(R.id.incrementGreenButton)
        val decrementGreenButton = layout.findViewById<ImageButton>(R.id.decrementGreenButton)
        val hexNumberGreen = layout.findViewById<TextView>(R.id.hexNumberGreen)
        incrementGreenButton.setOnClickListener {
            onUpClick(
                hexNumberGreen,
                layout,
                3
            )
        }
        decrementGreenButton.setOnClickListener {
            onDownClick(
                hexNumberGreen,
                layout,
                3
            )
        }
        repeatTouchListenersInitialize(incrementGreenButton, hexNumberGreen, layout, 3, true)
        repeatTouchListenersInitialize(decrementGreenButton, hexNumberGreen, layout, 3, false)
        val incrementBlueButton = layout.findViewById<ImageButton>(R.id.incrementBlueButton)
        val decrementBlueButton = layout.findViewById<ImageButton>(R.id.decrementBlueButton)
        val hexNumberBlue = layout.findViewById<TextView>(R.id.hexNumberBlue)
        incrementBlueButton.setOnClickListener {
            onUpClick(
                hexNumberBlue,
                layout,
                4
            )
        }
        decrementBlueButton.setOnClickListener {
            onDownClick(
                hexNumberBlue,
                layout,
                4
            )
        }
        repeatTouchListenersInitialize(incrementBlueButton, hexNumberBlue, layout, 4, true)
        repeatTouchListenersInitialize(decrementBlueButton, hexNumberBlue, layout, 4, false)
    }

    private fun initializeColorButtons(layout: View) {
        val colorPickerView: ColorPickerView = layout.findViewById(R.id.colorPickerView)
        setColorButtonListeners(colorPickerView, layout, R.id.color1)
        setColorButtonListeners(colorPickerView, layout, R.id.color2)
        setColorButtonListeners(colorPickerView, layout, R.id.color3)
        setColorButtonListeners(colorPickerView, layout, R.id.color4)
        setColorButtonListeners(colorPickerView, layout, R.id.color5)
        setColorButtonListeners(colorPickerView, layout, R.id.color6)
        setColorButtonListeners(colorPickerView, layout, R.id.color7)
        setColorButtonListeners(colorPickerView, layout, R.id.color8)
    }

    private fun setColorButtonListeners(
        colorPickerView: ColorPickerView,
        layout: View,
        colorFrmId: Int
    ) {
        val viewColor = layout.findViewById<View>(colorFrmId)
        val colorDrawable = viewColor.background as ColorDrawable
        viewColor.setOnClickListener {
            setColorOnClick(
                colorPickerView,
                layout,
                colorDrawable.color
            )
        }
    }

    private fun setColorOnClick(colorPickerView: ColorPickerView, layout: View, color: Int) {
        colorPickerView.setInitialColor(color)
        setSpinnerControls(layout, color)
        setSelectedColorFrame(layout)
    }

    private fun setSelectedColorFrame(layout: View) {
        val colorPickerView: ColorPickerView = layout.findViewById(R.id.colorPickerView)
        val selectedColor: Int = colorPickerView.color
        setColorFrameStatus(layout, selectedColor, R.id.color1, R.id.colorFrame1)
        setColorFrameStatus(layout, selectedColor, R.id.color2, R.id.colorFrame2)
        setColorFrameStatus(layout, selectedColor, R.id.color3, R.id.colorFrame3)
        setColorFrameStatus(layout, selectedColor, R.id.color4, R.id.colorFrame4)
        setColorFrameStatus(layout, selectedColor, R.id.color5, R.id.colorFrame5)
        setColorFrameStatus(layout, selectedColor, R.id.color6, R.id.colorFrame6)
        setColorFrameStatus(layout, selectedColor, R.id.color7, R.id.colorFrame7)
        setColorFrameStatus(layout, selectedColor, R.id.color8, R.id.colorFrame8)
    }

    private fun setColorFrameStatus(layout: View, selectedColor: Int, colorId: Int, frameId: Int) {
        val viewColor = layout.findViewById<View>(colorId)
        val colorDrawable = viewColor.background as ColorDrawable
        val viewColorFrame = layout.findViewById<View>(frameId)
        viewColorFrame.visibility =
            if (colorDrawable.color == selectedColor) View.VISIBLE else View.INVISIBLE
    }

    private fun onUpClick(hexNumber: TextView, layout: View, shiftRequired: Int) {
        onAmend(hexNumber, layout, shiftRequired, true)
    }

    private fun onDownClick(hexNumber: TextView, layout: View, shiftRequired: Int) {
        onAmend(hexNumber, layout, shiftRequired, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun repeatTouchListenersInitialize(
        imgButton: ImageButton,
        hexNumber: TextView,
        layout: View,
        shiftRequired: Int,
        increment: Boolean
    ) {
        imgButton.setOnTouchListener(object : OnTouchListener {
            private var mHandler: Handler? = null
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (mHandler != null) return true
                        mHandler = Handler()
                        mHandler!!.postDelayed(mAction, 500)
                    }
                    MotionEvent.ACTION_UP -> {
                        if (mHandler == null) return true
                        mHandler!!.removeCallbacks(mAction)
                        mHandler = null
                    }
                }
                return false
            }

            val mAction: Runnable = object : Runnable {
                override fun run() {
                    onAmend(hexNumber, layout, shiftRequired, increment)
                    mHandler!!.postDelayed(this, 100)
                }
            }
        })
    }

    private fun onAmend(hexNumber: TextView, layout: View, shiftRequired: Int, increment: Boolean) {
        val hex = hexNumber.text.toString().substring(2)
        var num = hex.toInt(16)
        if (increment) {
            if (num < 255) {
                num++
            }
        } else {
            if (num > 0) {
                num--
            }
        }
        var alteredHex = Integer.toHexString(num).uppercase()
        alteredHex = (if (num < 16) "0x0" else "0x") + alteredHex
        hexNumber.text = alteredHex
        val colorPickerView: ColorPickerView = layout.findViewById(R.id.colorPickerView)
        val color: Int = colorPickerView.color
        when (shiftRequired) {
            1 -> colorPickerView.setInitialColor(color and 0x00FFFFFF or (num shl 24))
            2 -> colorPickerView.setInitialColor(color and -0xff0001 or (num shl 16))
            3 -> colorPickerView.setInitialColor(color and -0xff01 or (num shl 8))
            4 -> colorPickerView.setInitialColor(color and -0x100 or num)
            else -> colorPickerView.setInitialColor(color and -0x100 or num)
        }
        setSelectedColorFrame(layout)
    }

    private fun setCustomTab(layout: View) {
        val colorPickerView: ColorPickerView = layout.findViewById(R.id.colorPickerView)
        colorPickerView.visibility = View.VISIBLE
        val customBtn = layout.findViewById<AppCompatButton>(R.id.custom)
        customBtn.setTypeface(null, Typeface.BOLD)
        customBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.pinkMagenta))


        val standardBtn = layout.findViewById<AppCompatButton>(R.id.standard)
        standardBtn.setTypeface(null, Typeface.NORMAL)
        standardBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.navyBlue))


        val spinnerControls = layout.findViewById<LinearLayout>(R.id.spinnerControls)
        spinnerControls.visibility = View.GONE
        val colorTableLayout = layout.findViewById<LinearLayout>(R.id.colorTableLayout)
        colorTableLayout.visibility = View.GONE
    }

    private fun setStandardTab(layout: View) {
        val colorPickerView: ColorPickerView = layout.findViewById(R.id.colorPickerView)
        colorPickerView.visibility = View.GONE
        val customBtn = layout.findViewById<AppCompatButton>(R.id.custom)
        customBtn.setTypeface(null, Typeface.NORMAL)
        customBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.navyBlue))
        val standardBtn = layout.findViewById<AppCompatButton>(R.id.standard)
        standardBtn.setTypeface(null, Typeface.BOLD)
        standardBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.pinkMagenta))
        val spinnerControls = layout.findViewById<LinearLayout>(R.id.spinnerControls)
        spinnerControls.visibility = View.VISIBLE
        setSpinnerControls(layout, colorPickerView.color)
        val colorTableLayout = layout.findViewById<LinearLayout>(R.id.colorTableLayout)
        colorTableLayout.visibility = View.VISIBLE
        setSelectedColorFrame(layout)
    }

    private fun setSpinnerControls(layout: View, color: Int) {
        val a = Color.alpha(color)
        val hexNumberOpacityTxtView = layout.findViewById<TextView>(R.id.hexNumberOpacity)
        hexNumberOpacityTxtView.text = formatIntIntoHex(a)
        val r = Color.red(color)
        val hexNumberRedTxtView = layout.findViewById<TextView>(R.id.hexNumberRed)
        hexNumberRedTxtView.text = formatIntIntoHex(r)
        val g = Color.green(color)
        val hexNumberGreenTxtView = layout.findViewById<TextView>(R.id.hexNumberGreen)
        hexNumberGreenTxtView.text = formatIntIntoHex(g)
        val b = Color.blue(color)
        val hexNumberBlueTxtView = layout.findViewById<TextView>(R.id.hexNumberBlue)
        hexNumberBlueTxtView.text = formatIntIntoHex(b)
    }

    private fun formatIntIntoHex(integer: Int): String {
        return (if (integer > 15) "0x" else "0x0") + Integer.toHexString(integer).uppercase()
    }

    abstract class ColorPickerObserver : ColorObserver {
        abstract fun onColorPicked(color: Int)
        override fun onColor(color: Int, fromUser: Boolean, shouldPropagate: Boolean) {}
    }

    init {
        context = builder.context
        initialColor = builder.initialColor
        enableBrightness = builder.enableBrightness
        enableAlpha = builder.enableAlpha
        okTitle = builder.okTitle
        cancelTitle = builder.cancelTitle
        showIndicator = builder.showIndicator
        showValue = builder.showValue
        onlyUpdateOnTouchEventUp = builder.onlyUpdateOnTouchEventUp
    }
}
