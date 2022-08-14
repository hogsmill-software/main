package com.example.frametext.engine.mainSizes

class SquareMainSizes(override val margin: Int) : MainSizes {
    override var width = 0
        private set
    override var height = 0
        private set

    override fun resetSizes(newWidthHeight: Int) {
        width = newWidthHeight
        height = newWidthHeight
    }
}