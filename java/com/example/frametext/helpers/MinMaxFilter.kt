package com.example.frametext.helpers

import android.text.InputFilter
import android.text.Spanned

class MinMaxFilter(minValue: String, maxValue: String) : InputFilter {
    private val mIntMin: Int
    private val mIntMax: Int
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dStart: Int,
        dEnd: Int
    ): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toInt()
            if (isInRange(mIntMin, mIntMax, input)) return null
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) ((c >= a) && (c <= b)) else ((c >= b) && (c <= a))
    }

    init {
        mIntMin = minValue.toInt()
        mIntMax = maxValue.toInt()
    }
}
