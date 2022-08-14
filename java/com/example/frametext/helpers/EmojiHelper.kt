package com.example.frametext.helpers

import android.graphics.Paint

object EmojiHelper {
    fun isCharEmojiAtPos(str: String, pos: Int): Boolean {
        val chr = str[pos]
        // chr == 0x270D is an annoying exception - it's the writing hand that can be coloured like other emojis
        // and has only 1 utf 8 character, instead of 2.
        return chr.code == 0xD83C || chr.code == 0xD83D || chr.code == 0xD83E || chr.code == 0x270D
    }

    // We already know we have an emoji
    fun emojiLengthAtPos(str: String, pos: Int): Int {
        val pos_incr =
            if (str[pos].code == 0x270D) pos + 1 else pos + 2 // Length 2 is min except for hand exception
        var new_pos = pos_incr
        var isCountryFlag = false // Used country as in computing flag has a different meaning.
        var activateSkinColorFilters = false
        if (pos + 1 < str.length) {
            val chr = str[pos + 1]
            if (chr.code > 0xDDE0 && chr.code <= 0xDDFF) {
                isCountryFlag = true
            }
        }
        while (true) {
            if (new_pos >= str.length) {
                break // Security but should never get here.
            }
            val chr = str[new_pos]
            if (chr.code == 0xFE0F) {
                new_pos++
            } else if (chr.code == 0x200D) {
                new_pos += 3
            } else if (isCountryFlag) {
                if (new_pos == pos + 2) {
                    if (chr.code == 0xD83C) {
                        new_pos++
                    } else {
                        break
                    }
                } else if (new_pos == pos + 3) {
                    if (chr.code > 0xDDE0 && chr.code <= 0xDDFF) {
                        new_pos++
                    } else {
                        new_pos--
                    }
                    break
                }
            } else if (new_pos == pos_incr && chr.code == 0xD83C) { // colored body parts handled here
                new_pos++
                activateSkinColorFilters = true
            } else if (activateSkinColorFilters) {
                activateSkinColorFilters = if (chr.code >= 0xDFFB && chr.code <= 0xDFFF) {
                    new_pos++
                    false
                } else {
                    new_pos--
                    break
                }
            } else {
                break
            }
        }
        return new_pos - pos
    }

    fun getEmojiWidth(str: String, pos: Int, len: Int, paint: Paint): Float {
        return paint.measureText(str.substring(pos, pos + len))
    }
}


