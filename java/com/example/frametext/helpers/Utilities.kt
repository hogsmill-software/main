package com.example.frametext.helpers

import android.content.Context
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.WindowManager
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
import com.google.android.material.tabs.TabLayout
import java.util.ArrayList

object Utilities {
    private var tabLayout: TabLayout? = null
    fun setTabLayout(tabLayout_: TabLayout) {
        tabLayout = tabLayout_
    }

    fun getTotalTopHeight(context: Context): Int {
        val realScreenSize: Point = getRealScreenSize(context)
        var topHeight = 0
        val res = context.resources
        if (res != null) {
            val metrics = res.displayMetrics
            if (metrics != null) {
                val mhp = metrics.heightPixels
                val mwp = metrics.widthPixels
                topHeight += if (mhp == realScreenSize.y) {
                    realScreenSize.x - mwp
                } else {
                    realScreenSize.y - mhp
                }
            }
        }
        if (tabLayout != null) {
            topHeight += tabLayout!!.height
        }

        // Arbitrary correction - seems to give best results across 4 emulators;
        topHeight -= (convertDpToPixel(
            32f,
            context
        ) - 32).toInt()
        return topHeight
    }

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

    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
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
}