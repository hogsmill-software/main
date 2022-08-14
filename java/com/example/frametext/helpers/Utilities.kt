package com.example.frametext.helpers

import android.content.Context
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.WindowManager
import com.google.android.material.tabs.TabLayout

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
}