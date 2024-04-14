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
import com.example.frametext.helpers.Constants.UFFF_AKRONIM
import com.example.frametext.helpers.Constants.UFFF_BUTTERFLY_KIDS
import com.example.frametext.helpers.Constants.UFFF_DANCING_SCRIPT
import com.example.frametext.helpers.Constants.UFFF_EUPHORIA_SCRIPT
import com.example.frametext.helpers.Constants.UFFF_FASTER_ONE
import com.example.frametext.helpers.Constants.UFFF_FRIJOLE
import com.example.frametext.helpers.Constants.UFFF_JACQUES_FRANCOIS_SHADOW
import com.example.frametext.helpers.Constants.UFFF_LOBSTER
import com.example.frametext.helpers.Constants.UFFF_LOVERS_QUARREL
import com.example.frametext.helpers.Constants.UFFF_MISS_FAJARDOSE
import com.example.frametext.helpers.Constants.UFFF_MONOFETT
import com.example.frametext.helpers.Constants.UFFF_MONOSPACE
import com.example.frametext.helpers.Constants.UFFF_MONSIEUR_LA_DOULAISE
import com.example.frametext.helpers.Constants.UFFF_MONTE_CARLO
import com.example.frametext.helpers.Constants.UFFF_NORMAL
import com.example.frametext.helpers.Constants.UFFF_NOSIFER
import com.example.frametext.helpers.Constants.UFFF_NOTOSERIF
import com.example.frametext.helpers.Constants.UFFF_OLD_LONDON
import com.example.frametext.helpers.Constants.UFFF_PUPPIES_PLAY
import com.example.frametext.helpers.Constants.UFFF_RAMPART_ONE
import com.example.frametext.helpers.Constants.UFFF_ROBOTO
import com.example.frametext.helpers.Constants.UFFF_SANS_SERIF
import com.example.frametext.helpers.Constants.UFFF_SANS_SERIF_CONDENSED
import com.example.frametext.helpers.Constants.UFFF_SANS_SERIF_LIGHT
import com.example.frametext.helpers.Constants.UFFF_SANS_SERIF_MEDIUM
import com.example.frametext.helpers.Constants.UFFF_SANS_SERIF_THIN
import com.example.frametext.helpers.Constants.UFFF_SERIF
import com.example.frametext.helpers.Constants.UFFF_SHADOWS_INTO_LIGHT
import com.example.frametext.helpers.Constants.UFFF_UNIFRAKTUR_COOK
import com.example.frametext.helpers.Constants.UFFF_UNIFRAKTUR_MAGUNTIA
import com.example.frametext.helpers.Constants.UFFF_VAST_SHADOW
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
        userFriendlyFontFamilyList.add(UFFF_AKRONIM)
        userFriendlyFontFamilyList.add(UFFF_BUTTERFLY_KIDS)
        userFriendlyFontFamilyList.add(UFFF_DANCING_SCRIPT)
        userFriendlyFontFamilyList.add(UFFF_EUPHORIA_SCRIPT)
        userFriendlyFontFamilyList.add(UFFF_FASTER_ONE)
        userFriendlyFontFamilyList.add(UFFF_FRIJOLE)
        userFriendlyFontFamilyList.add(UFFF_JACQUES_FRANCOIS_SHADOW)
        userFriendlyFontFamilyList.add(UFFF_LOBSTER)
        userFriendlyFontFamilyList.add(UFFF_LOVERS_QUARREL)
        userFriendlyFontFamilyList.add(UFFF_MISS_FAJARDOSE)
        userFriendlyFontFamilyList.add(UFFF_MONOFETT)
        userFriendlyFontFamilyList.add(UFFF_MONSIEUR_LA_DOULAISE)
        userFriendlyFontFamilyList.add(UFFF_MONTE_CARLO)
        userFriendlyFontFamilyList.add(UFFF_NOSIFER)
        userFriendlyFontFamilyList.add(UFFF_OLD_LONDON)
        userFriendlyFontFamilyList.add(UFFF_PUPPIES_PLAY)
        userFriendlyFontFamilyList.add(UFFF_RAMPART_ONE)
        userFriendlyFontFamilyList.add(UFFF_SHADOWS_INTO_LIGHT)
        userFriendlyFontFamilyList.add(UFFF_UNIFRAKTUR_COOK)
        userFriendlyFontFamilyList.add(UFFF_UNIFRAKTUR_MAGUNTIA)
        userFriendlyFontFamilyList.add(UFFF_VAST_SHADOW)
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

    fun userFriendlyFontFamilyToTypeFaceId(): HashMap<String, Int> {
        val userFriendlyFontFamilyToTypeFaceId = HashMap<String, Int>()
        userFriendlyFontFamilyToTypeFaceId[UFFF_AKRONIM] = R.font.akronim
        userFriendlyFontFamilyToTypeFaceId[UFFF_BUTTERFLY_KIDS] = R.font.butterfly_kids
        userFriendlyFontFamilyToTypeFaceId[UFFF_DANCING_SCRIPT] = R.font.dancing_script
        userFriendlyFontFamilyToTypeFaceId[UFFF_EUPHORIA_SCRIPT] = R.font.euphoria_script
        userFriendlyFontFamilyToTypeFaceId[UFFF_FASTER_ONE] = R.font.faster_one
        userFriendlyFontFamilyToTypeFaceId[UFFF_FRIJOLE] = R.font.frijole
        userFriendlyFontFamilyToTypeFaceId[UFFF_JACQUES_FRANCOIS_SHADOW] = R.font.jacques_francois_shadow
        userFriendlyFontFamilyToTypeFaceId[UFFF_LOBSTER] = R.font.lobster
        userFriendlyFontFamilyToTypeFaceId[UFFF_LOVERS_QUARREL] = R.font.lovers_quarrel
        userFriendlyFontFamilyToTypeFaceId[UFFF_MISS_FAJARDOSE] = R.font.miss_fajardose
        userFriendlyFontFamilyToTypeFaceId[UFFF_MONOFETT] = R.font.monofett
        userFriendlyFontFamilyToTypeFaceId[UFFF_MONSIEUR_LA_DOULAISE] = R.font.monsieur_la_doulaise
        userFriendlyFontFamilyToTypeFaceId[UFFF_MONTE_CARLO] = R.font.monte_carlo
        userFriendlyFontFamilyToTypeFaceId[UFFF_NOSIFER] = R.font.nosifer
        userFriendlyFontFamilyToTypeFaceId[UFFF_OLD_LONDON] = R.font.old_london
        userFriendlyFontFamilyToTypeFaceId[UFFF_PUPPIES_PLAY] = R.font.puppies_play
        userFriendlyFontFamilyToTypeFaceId[UFFF_RAMPART_ONE] = R.font.rampart_one
        userFriendlyFontFamilyToTypeFaceId[UFFF_SHADOWS_INTO_LIGHT] = R.font.shadows_into_light
        userFriendlyFontFamilyToTypeFaceId[UFFF_UNIFRAKTUR_COOK] = R.font.unifraktur_cook
        userFriendlyFontFamilyToTypeFaceId[UFFF_UNIFRAKTUR_MAGUNTIA] = R.font.unifraktur_maguntia
        userFriendlyFontFamilyToTypeFaceId[UFFF_VAST_SHADOW] = R.font.vast_shadow
        return userFriendlyFontFamilyToTypeFaceId
    }

    fun typeFaceIdToUserFriendlyFontFamily(): HashMap<Int, String> {
        val typeFaceIdToUserFriendlyFontFamily = HashMap<Int, String>()
        typeFaceIdToUserFriendlyFontFamily[R.font.akronim] = UFFF_AKRONIM
        typeFaceIdToUserFriendlyFontFamily[R.font.butterfly_kids] = UFFF_BUTTERFLY_KIDS
        typeFaceIdToUserFriendlyFontFamily[R.font.dancing_script] = UFFF_DANCING_SCRIPT
        typeFaceIdToUserFriendlyFontFamily[R.font.euphoria_script] = UFFF_EUPHORIA_SCRIPT
        typeFaceIdToUserFriendlyFontFamily[R.font.faster_one] = UFFF_FASTER_ONE
        typeFaceIdToUserFriendlyFontFamily[R.font.frijole] = UFFF_FRIJOLE
        typeFaceIdToUserFriendlyFontFamily[R.font.jacques_francois_shadow] = UFFF_JACQUES_FRANCOIS_SHADOW
        typeFaceIdToUserFriendlyFontFamily[R.font.lobster] = UFFF_LOBSTER
        typeFaceIdToUserFriendlyFontFamily[R.font.lovers_quarrel] = UFFF_LOVERS_QUARREL
        typeFaceIdToUserFriendlyFontFamily[R.font.miss_fajardose] =UFFF_MISS_FAJARDOSE
        typeFaceIdToUserFriendlyFontFamily[R.font.monofett] =UFFF_MONOFETT
        typeFaceIdToUserFriendlyFontFamily[R.font.monsieur_la_doulaise] =UFFF_MONSIEUR_LA_DOULAISE
        typeFaceIdToUserFriendlyFontFamily[R.font.monte_carlo] =UFFF_MONTE_CARLO
        typeFaceIdToUserFriendlyFontFamily[R.font.nosifer] =UFFF_NOSIFER
        typeFaceIdToUserFriendlyFontFamily[R.font.old_london] = UFFF_OLD_LONDON
        typeFaceIdToUserFriendlyFontFamily[R.font.puppies_play] =UFFF_PUPPIES_PLAY
        typeFaceIdToUserFriendlyFontFamily[R.font.rampart_one] =UFFF_RAMPART_ONE
        typeFaceIdToUserFriendlyFontFamily[R.font.shadows_into_light] = UFFF_SHADOWS_INTO_LIGHT
        typeFaceIdToUserFriendlyFontFamily[R.font.unifraktur_cook] =UFFF_UNIFRAKTUR_COOK
        typeFaceIdToUserFriendlyFontFamily[R.font.unifraktur_maguntia] =UFFF_UNIFRAKTUR_MAGUNTIA
        typeFaceIdToUserFriendlyFontFamily[R.font.vast_shadow] =UFFF_VAST_SHADOW
        return typeFaceIdToUserFriendlyFontFamily
    }

    @Suppress("UNUSED_PARAMETER")
    fun closestDistance(useEmoji: Boolean, emoji: String, symbol: String?, symbolShapeType: SymbolShapeType): Int {
        // parameter emoji shall almost certainly be used in future
        if (useEmoji)
            return 250
        else if (symbol.isNullOrEmpty()) {
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

    fun getDisabledTextColorId(context: Context) : Int {
        return when (context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> R.color.disabledTextDark
            Configuration.UI_MODE_NIGHT_NO -> R.color.disabledText
            Configuration.UI_MODE_NIGHT_UNDEFINED -> R.color.disabledText
            else -> R.color.disabledText
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
    fun getPinkMagentaColorId(context: Context) : Int {
        return when (context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> R.color.pinkMagentaDarkMode
            Configuration.UI_MODE_NIGHT_NO -> R.color.pinkMagenta
            Configuration.UI_MODE_NIGHT_UNDEFINED -> R.color.pinkMagenta
            else -> R.color.pinkMagenta
        }
    }
}
// From https://medium.com/android-news/how-to-remove-all-from-your-kotlin-code-87dc2c9767fb see section 4
fun <T1, T2> ifNotNull(value1: T1?, value2: T2?, bothNotNull: (T1, T2) -> (Unit)) {
    if (value1 != null && value2 != null) {
        bothNotNull(value1, value2)
    }
}