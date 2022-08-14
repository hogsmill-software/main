package com.example.frametext.engine

//import Hyphens.HyphenatePatternsLanguage;
class TextFormattingDetails(
    val contentText: String,
    val optimizeSpacing: Boolean,
    hyphenateText: Boolean,
    val hyphenPatternLan: String?,
    val topTextMargin: Int,
    val lineHeight: Int, // The margin between heart frame and actual text. Can be 0.
    var txtHeartsMargin: Int,
    txtColor: Int
) {
     var width = hyphenPatternLan
        private set

    var hyphenateText = false
    val txtColour: Int

    init {
        txtHeartsMargin = txtHeartsMargin
        txtColour = txtColor
        if (hyphenPatternLan == null) {
            this.hyphenateText = false
        } else {
            this.hyphenateText = hyphenateText
        }
    }
}