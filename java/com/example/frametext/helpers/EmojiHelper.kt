package com.example.frametext.helpers

//import android.graphics.Paint

object EmojiHelper {
    fun isCharEmojiAtPos(str: String, pos: Int): Boolean {
        val chr = str[pos]
        // chr == 0x270D is an annoying exception - it's the writing hand that can be coloured like other emojis
        // and has only 1 utf 8 character, instead of 2.
        return chr.code == 0xD83C || chr.code == 0xD83D || chr.code == 0xD83E || chr.code == 0x270D
    }

    // We already know we have an emoji
    fun emojiLengthAtPos(str: String, pos: Int): Int {
        val posIncrement =
            if (str[pos].code == 0x270D) pos + 1 else pos + 2 // Length 2 is min except for hand exception
        var newPos = posIncrement
        var isCountryFlag = false // Used country as in computing flag has a different meaning.
        var activateSkinColorFilters = false
        if (pos + 1 < str.length) {
            val chr = str[pos + 1]
            if (chr.code in 0xdde1..0xDDFF) {
                isCountryFlag = true
            }
        }
        while (true) {
            if (newPos >= str.length) {
                break // Security but should never get here.
            }
            val chr = str[newPos]
            if (chr.code == 0xFE0F) {
                newPos++
            } else if (chr.code == 0x200D) {
                newPos += 3
            } else if (isCountryFlag) {
                if (newPos == pos + 2) {
                    if (chr.code == 0xD83C) {
                        newPos++
                    } else {
                        break
                    }
                } else if (newPos == pos + 3) {
                    if (chr.code in 0xdde1..0xDDFF) {
                        newPos++
                    } else {
                        newPos--
                    }
                    break
                }
            } else if (newPos == posIncrement && chr.code == 0xD83C) { // colored body parts handled here
                newPos++
                activateSkinColorFilters = true
            } else if (activateSkinColorFilters) {
                activateSkinColorFilters = if (chr.code in 0xDFFB..0xDFFF) {
                    newPos++
                    false
                } else {
                    newPos--
                    break
                }
            } else {
                break
            }
        }
        return newPos - pos
    }

    /* currently not used, but might be...
    fun getEmojiWidth(str: String, pos: Int, len: Int, paint: Paint): Float {
        return paint.measureText(str.substring(pos, pos + len))
    }
    */
}


