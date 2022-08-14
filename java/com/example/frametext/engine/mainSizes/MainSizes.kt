package com.example.frametext.engine.mainSizes

// This interface is for the global size of image
interface MainSizes {
    fun resetSizes(newWidth: Int)
    val width: Int
    val height: Int
    val margin: Int
}
