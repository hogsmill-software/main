package com.example.frametext.engine.mainSizes

class HeartMainSizes(override val margin: Int) : MainSizes {
    override var width = 0
        private set
    override var height = 0
        private set
    var radius = 0
        private set

    override fun resetSizes(newWidth: Int) {
        width = newWidth
        val widthInsideFrame = width - 2 * margin
        height = (0.96 * widthInsideFrame).toInt() + 2 * margin
        radius = (widthInsideFrame * 0.28).toInt()
    }

}
