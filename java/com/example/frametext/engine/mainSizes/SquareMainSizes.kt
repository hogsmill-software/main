package com.example.frametext.engine.mainSizes

class SquareMainSizes(override val margin: Int) : MainSizes {
    override var width = 0
    override var height = 0

    override fun resetSizes(newWidth: Int) {
        width = newWidth
        height = newWidth
    }
}