package com.example.frametext.helpers

import android.text.InputFilter
import android.text.Spanned

// Inspired from https://www.geeksforgeeks.org/how-to-set-minimum-and-maximum-input-value-in-edittext-in-android/?ref=rp
// However, that link has code riddled with bugs. I've told them about these. This code is BETTER.
// For function pointers, used https://stackoverflow.com/questions/16120697/kotlin-how-to-pass-a-function-as-parameter-to-another
// minValue is the minimum value of input allowed. Zero recommended.
// maxValue is the maximum value of input allowed.
// onValidFuncPtr points to a function that has code to be executed for valid input entries.
// onBlankFuncPtr points to a function that has code to be executed when line is empty and user has deleted everything.

class MinMaxFilter(private val minValue: Int, private val maxValue: Int, val onValidFuncPtr: ((Int) -> Unit), val onBlankFuncPtr: () -> Unit) :
    InputFilter {

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dStart: Int, dEnd: Int): CharSequence? {
        try {
            val existingAfterRemoval = dest.toString().removeRange(dStart, dEnd)
            val lengthRemoved = dEnd - dStart
            val newEnd = dEnd - lengthRemoved
            val inputAsStr = StringBuilder(existingAfterRemoval).insert(newEnd, source.toString()).toString()

            if (inputAsStr.isEmpty()) {
                if (start == 0 && end == 0 && dStart == 0) {
                    // We enter this code if deleted everything
                    onBlankFuncPtr()
                }
            }
            else {
                val input = Integer.parseInt(inputAsStr)
                if (isInRange(minValue, maxValue, input)) {
                    onValidFuncPtr(input)
                    return null
                }
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) c in a..b else c in b..a
    }
}
