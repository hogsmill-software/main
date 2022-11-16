package com.example.frametext.engine

import android.graphics.Rect

class TextRectDetails(val boundingRect: Rect) {
    fun setEndOfLine() {
        endOfLine = true
    }

    var text: String = String()
    var textWidth = 0
    private var endOfLine = false

}
