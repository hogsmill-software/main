package com.example.frametext.engine

import com.example.frametext.enums.TextAlignment

class TextFormattingDetails(
    val contentText: String,
    val optimizeSpacing: Boolean,
    hyphenateText: Boolean,
    val hyphenPatternLan: String?,
    val topTextMargin: Int,
    val lineHeight: Int, // The margin between heart frame and actual text. Can be 0.
    val txtHeartsMargin: Int,
    val txtColor: Int,
    val fontFamily: String,
    val typeFaceId: Int,
    val fontStyle: Int,
    val textAlignmemt: TextAlignment
) {
    var hyphenateText = false
        private set

    init {
        if (hyphenPatternLan == null) {
            this.hyphenateText = false
        } else {
            this.hyphenateText = hyphenateText
        }
    }
}