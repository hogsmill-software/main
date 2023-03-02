package com.example.frametext.helpers

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.WindowManager
import com.example.frametext.R
import com.example.frametext.enums.SymbolShapeType
import com.example.frametext.helpers.Constants.FF_MONOSPACE
import com.example.frametext.helpers.Constants.FF_NORMAL
import com.example.frametext.helpers.Constants.FF_NOTOSERIF
import com.example.frametext.helpers.Constants.FF_ROBOTO
import com.example.frametext.helpers.Constants.FF_SANS_SERIF
import com.example.frametext.helpers.Constants.FF_SANS_SERIF_CONDENSED
import com.example.frametext.helpers.Constants.FF_SANS_SERIF_LIGHT
import com.example.frametext.helpers.Constants.FF_SANS_SERIF_MEDIUM
import com.example.frametext.helpers.Constants.FF_SANS_SERIF_THIN
import com.example.frametext.helpers.Constants.FF_SERIF
import com.example.frametext.helpers.Constants.UFFF_MONOSPACE
import com.example.frametext.helpers.Constants.UFFF_NORMAL
import com.example.frametext.helpers.Constants.UFFF_NOTOSERIF
import com.example.frametext.helpers.Constants.UFFF_ROBOTO
import com.example.frametext.helpers.Constants.UFFF_SANS_SERIF
import com.example.frametext.helpers.Constants.UFFF_SANS_SERIF_CONDENSED
import com.example.frametext.helpers.Constants.UFFF_SANS_SERIF_LIGHT
import com.example.frametext.helpers.Constants.UFFF_SANS_SERIF_MEDIUM
import com.example.frametext.helpers.Constants.UFFF_SANS_SERIF_THIN
import com.example.frametext.helpers.Constants.UFFF_SERIF
import java.util.ArrayList

object Utilities {
    fun getRealScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        return size
    }

    // 2 methods below from  https://androiddvlpr.com/android-dp-to-px/
    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun userFriendlyFontFamilyList(): ArrayList<String>{
        val userFriendlyFontFamilyList: ArrayList<String> = ArrayList<String>()
        userFriendlyFontFamilyList.add(UFFF_MONOSPACE)
        userFriendlyFontFamilyList.add(UFFF_NORMAL)
        userFriendlyFontFamilyList.add(UFFF_NOTOSERIF)
        userFriendlyFontFamilyList.add(UFFF_ROBOTO)
        userFriendlyFontFamilyList.add(UFFF_SANS_SERIF)
        userFriendlyFontFamilyList.add(UFFF_SANS_SERIF_LIGHT)
        userFriendlyFontFamilyList.add(UFFF_SANS_SERIF_THIN)
        userFriendlyFontFamilyList.add(UFFF_SANS_SERIF_CONDENSED)
        userFriendlyFontFamilyList.add(UFFF_SANS_SERIF_MEDIUM)
        userFriendlyFontFamilyList.add(UFFF_SERIF)
        return userFriendlyFontFamilyList
    }

    fun userFriendlyFontFamilyToFontFamilyHashMap(): HashMap<String, String> {
        val userFriendlyFontFamilyToFontFamilyHashMap = HashMap<String, String>()
        userFriendlyFontFamilyToFontFamilyHashMap[UFFF_MONOSPACE] = FF_MONOSPACE
        userFriendlyFontFamilyToFontFamilyHashMap[UFFF_NORMAL] = FF_NORMAL
        userFriendlyFontFamilyToFontFamilyHashMap[UFFF_NOTOSERIF] = FF_NOTOSERIF
        userFriendlyFontFamilyToFontFamilyHashMap[UFFF_ROBOTO] = FF_ROBOTO
        userFriendlyFontFamilyToFontFamilyHashMap[UFFF_SANS_SERIF] = FF_SANS_SERIF
        userFriendlyFontFamilyToFontFamilyHashMap[UFFF_SANS_SERIF_LIGHT] = FF_SANS_SERIF_LIGHT
        userFriendlyFontFamilyToFontFamilyHashMap[UFFF_SANS_SERIF_THIN] = FF_SANS_SERIF_THIN
        userFriendlyFontFamilyToFontFamilyHashMap[UFFF_SANS_SERIF_CONDENSED] = FF_SANS_SERIF_CONDENSED
        userFriendlyFontFamilyToFontFamilyHashMap[UFFF_SANS_SERIF_MEDIUM] = FF_SANS_SERIF_MEDIUM
        userFriendlyFontFamilyToFontFamilyHashMap[UFFF_SERIF] = FF_SERIF
        return userFriendlyFontFamilyToFontFamilyHashMap
    }

    fun fontFamilyToUserFriendlyFontFamilyHashMap(): HashMap<String, String> {
        val fontFamilyToUserFriendlyFontFamilyHashMap = HashMap<String, String>()
        fontFamilyToUserFriendlyFontFamilyHashMap[FF_MONOSPACE] = UFFF_MONOSPACE
        fontFamilyToUserFriendlyFontFamilyHashMap[FF_NORMAL] = UFFF_NORMAL
        fontFamilyToUserFriendlyFontFamilyHashMap[FF_NOTOSERIF] = UFFF_NOTOSERIF
        fontFamilyToUserFriendlyFontFamilyHashMap[FF_ROBOTO] = UFFF_ROBOTO
        fontFamilyToUserFriendlyFontFamilyHashMap[FF_SANS_SERIF] = UFFF_SANS_SERIF
        fontFamilyToUserFriendlyFontFamilyHashMap[FF_SANS_SERIF_LIGHT] = UFFF_SANS_SERIF_LIGHT
        fontFamilyToUserFriendlyFontFamilyHashMap[FF_SANS_SERIF_THIN] = UFFF_SANS_SERIF_THIN
        fontFamilyToUserFriendlyFontFamilyHashMap[FF_SANS_SERIF_CONDENSED] = UFFF_SANS_SERIF_CONDENSED
        fontFamilyToUserFriendlyFontFamilyHashMap[FF_SANS_SERIF_MEDIUM] = UFFF_SANS_SERIF_MEDIUM
        fontFamilyToUserFriendlyFontFamilyHashMap[FF_SERIF] = UFFF_SERIF
        return fontFamilyToUserFriendlyFontFamilyHashMap
    }
    @Suppress("UNUSED_PARAMETER")
    fun closestDistance(useEmoji: Boolean, emoji: String, symbol: String?, symbolShapeType: SymbolShapeType): Int {
        // parameter emoji shall almost certainly be used in future
        if (useEmoji)
            return 250
        else if (symbol == null || symbol.isEmpty()) {
            val closestDistance = when (symbolShapeType) {
                SymbolShapeType.Circle -> 150
                SymbolShapeType.Star -> 150
                SymbolShapeType.Square -> 150
                SymbolShapeType.Heart -> 150
                SymbolShapeType.Spade -> 150
                SymbolShapeType.Club -> 150
                SymbolShapeType.Diamond -> 150
                SymbolShapeType.Smiley -> 150
                SymbolShapeType.None -> 150
            }
            return closestDistance
        }
        else {
            val closestDistance = when (symbol) {
                "█" -> 250
                "●" -> 200
                "▬" -> 200
                "★" -> 180
                else -> 150
            }
            return closestDistance
        }
    }

    fun getTextColorId(context: Context) : Int {
        return when (context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> R.color.white
                Configuration.UI_MODE_NIGHT_NO -> R.color.black
                Configuration.UI_MODE_NIGHT_UNDEFINED -> R.color.black
                else -> R.color.black
            }
    }

    fun getBackgroundColorId(context: Context) : Int {
        return when (context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> R.color.black
            Configuration.UI_MODE_NIGHT_NO -> R.color.white
            Configuration.UI_MODE_NIGHT_UNDEFINED -> R.color.white
            else -> R.color.white
        }
    }

    fun getFrameColorId(context: Context) : Int {
        return when (context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> R.color.darkmodeFrameColor
            Configuration.UI_MODE_NIGHT_NO -> R.color.highlightBlue
            Configuration.UI_MODE_NIGHT_UNDEFINED -> R.color.highlightBlue
            else -> R.color.highlightBlue
        }
    }
}
// From https://medium.com/android-news/how-to-remove-all-from-your-kotlin-code-87dc2c9767fb see section 4
fun <T1, T2> ifNotNull(value1: T1?, value2: T2?, bothNotNull: (T1, T2) -> (Unit)) {
    if (value1 != null && value2 != null) {
        bothNotNull(value1, value2)
    }
}