package com.example.frametext.engine

class TextInputDetails internal constructor() {
    fun initialise(contentText: String) {
        val lineDetailsArray = contentText.split("\\r?\\n".toRegex()).toTypedArray()
        for (str in lineDetailsArray) {
            lineDetailsLst.add(TextLineDetails(str))
        }
        this.isInitialized = true
    }

    fun reset() {
        lineDetailsLst.clear()
        this.isInitialized = false
    }

    fun resetAllTextFits() {
        for (lineDetails in lineDetailsLst) {
            lineDetails.allTextFits = true
        }
    }

    fun allTextFits(): Boolean {
        for (i in lineDetailsLst.indices) {
            if (!lineDetailsLst[i].allTextFits) {
                return false
            }
        }
        return true
    }

    fun getLineDetails(idx: Int): TextLineDetails {
        return lineDetailsLst[idx]
    }

    fun count(): Int {
        return lineDetailsLst.size
    }

    private val lineDetailsLst: MutableList<TextLineDetails> = ArrayList()
    var isInitialized = false
        private set

}