package com.example.frametext.globalObjects

import android.graphics.Typeface
import com.example.frametext.enums.MainShapeType
import com.example.frametext.enums.SymbolShapeType
import com.example.frametext.enums.TextAlignment
import com.example.frametext.helpers.Constants.FF_NORMAL
import com.example.frametext.shapes.edge.*

class FrameTextParameters {
    var hyphenFileName: String? = null
    var optimizeSpacing = true
    var hyphenateText = false
    private var txtSymbolsMargin = 20
    private val maxTxtSymbolsMargin = 50
    var outerMargin = 15
    var textColor = -0x1000000
    var symbolsColor = -0x10000
    var backgroundColor = -0x1
    var useEmoji = false
    var emoji = 0x2665.toChar().toString()
    var symbolShapeType: SymbolShapeType = SymbolShapeType.None
    var mainShapeType: MainShapeType = MainShapeType.Heart
    var symbol: String? = "\u2665\uFE0E"
    var fontFamily: String = FF_NORMAL
    var typefaceId: Int = 0
    var fontStyle = Typeface.NORMAL
    var minDistEdgeShape = 0
    var textAlignment: TextAlignment = TextAlignment.Left

    fun setTxtSymbolsMargin(txtSymbolsMargin: Int) {
        if (txtSymbolsMargin <= maxTxtSymbolsMargin) this.txtSymbolsMargin = txtSymbolsMargin
    }

    fun getTxtSymbolsMargin(): Int {
        return txtSymbolsMargin
    }

    fun getShapeDetails(): EdgeShapeDetails? {
        if (useEmoji) {
            return EmojiEdgeShapeDetails(emoji)
        } else symbol?.let {
            if (symbolShapeType == SymbolShapeType.None) {
                return SymbolEdgeShapeDetails(it, symbolsColor)
            }
        }

        return when (symbolShapeType) {
            SymbolShapeType.Heart -> DrawHeartEdgeShape(92, symbolsColor)
            SymbolShapeType.Circle -> DrawCircleEdgeShape(92, symbolsColor)
            SymbolShapeType.Square -> DrawSquareEdgeShape(92, symbolsColor)
            SymbolShapeType.Star -> DrawStarEdgeShape(92, symbolsColor)
            SymbolShapeType.Spade -> DrawSpadeEdgeShape(92, symbolsColor)
            SymbolShapeType.Club -> DrawClubEdgeShape(92, symbolsColor)
            SymbolShapeType.Diamond -> DrawDiamondEdgeShape(92, symbolsColor)
            SymbolShapeType.Smiley -> DrawSmileyEdgeShape(92, symbolsColor)
            else -> { null }
        }

    }
}