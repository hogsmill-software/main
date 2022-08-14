package com.example.frametext.engine.textBoundaries

import com.example.frametext.engine.TextRectDetails

interface TextBoundaries {
    fun computeTextRectangles(): List<TextRectDetails>
}