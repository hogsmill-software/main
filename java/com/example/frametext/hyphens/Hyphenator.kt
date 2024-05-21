package com.example.frametext.hyphens

import android.content.Context
import kotlin.math.min

class Hyphenator(language: String, context: Context) {
    private fun loadPatterns(language: String, context: Context) {
        val rhl = ResourceHyphenatePatternsLoader(language, context)
        rhl.load()
        populatePatternsMapFromList(rhl.getPatterns())
        populateExceptionsMapFromList(rhl.getExceptions())
    }

    private fun populateExceptionsMapFromList(exceptions: List<String>) {
        for (str in exceptions) {
            val lst = str.split("-".toRegex()).toTypedArray()
            val stringBuilderNoHyphen = StringBuilder()
            for (s in lst) {
                stringBuilderNoHyphen.append(s)
            }
            this.exceptions[stringBuilderNoHyphen.toString()] = lst
        }
    }

    fun addException(lst: Array<String>) {
        val stringBuilderNoHyphen = StringBuilder()
        for (s in lst) {
            stringBuilderNoHyphen.append(s)
        }
        exceptions[stringBuilderNoHyphen.toString()] = lst
    }

    private fun populatePatternsMapFromList(patterns: List<String>) {
        val sb = StringBuilder()
        for (str in patterns) {
            var idx = 0
            val ints = IntArray(str.length)
            for (i in str.indices) {
                if (Character.isDigit(str[i])) {
                    ints[idx] = Character.getNumericValue(str[i])
                } else {
                    sb.append(str[i])
                    idx++
                }
            }
            val syllable = sb.toString()
            sb.setLength(0)
            val ints2 = IntArray(syllable.length + 1)
            val intsLength = min(ints.size, ints2.size)
            System.arraycopy(ints, 0, ints2, 0, intsLength)
            this.patterns[syllable] = ints2
        }
    }

    fun hyphenateWord(word: String): Array<String> {
        if (exceptions.containsKey(word)) {
            val strArr = exceptions[word] as Array<String>
            return strArr.clone() // Need to make clone otherwise you can tamper with an exception stored here.
        }
        if (word.length < minLength) {
            return Array(1) { word }
        }

        val hyphenIntArr = getHyphenateIntArrayFromPatterns(word)
        var startWordIdx = 0
        val hyphens = ArrayList<String>()
        for (idx in hyphenIntArr.indices) {
            if (hyphenIntArr[idx] % 2 != 0) {
                hyphens.add(word.substring(startWordIdx, idx - 1))
                startWordIdx = idx - 1
            }
        }
        hyphens.add(word.substring(startWordIdx))
        return hyphens.toTypedArray()
    }

    // Takes a word.
    // Using Knuth Liang algorithm, returns a boolean array of the hyphen points.
    private fun getHyphenateIntArrayFromPatterns(word: String): IntArray {
        val hyphenableWord =
            StringBuilder().append(endsMarker).append(word).append(endsMarker).toString()
        val levels = IntArray(hyphenableWord.length)
        for (i in 0 until hyphenableWord.length - 2) {
            for (j in i + 1 until hyphenableWord.length) {
                val subWord = hyphenableWord.substring(i, j)
                if (patterns.containsKey(subWord)) {
                    val subLevels = patterns[subWord]
                    subLevels?.let { subLevelsIt ->
                        for (k in subLevelsIt.indices) {
                            if (subLevelsIt[k] > levels[i + k]) {
                                levels[i + k] = subLevelsIt[k]
                            }
                        }
                    }
                }
            }
        }

        // I have assumed here that for all languages 1st and last character cannot be hyphenated.
        // This may be language dependent?
        for (i in 0..minLeading) {
            levels[i] = 0
        }
        for (i in levels.size - minTrailing + 1 until levels.size) {
            levels[i] = 0
        }
        return levels
    }

    private val minLength: Int
    private val minLeading: Int
    private val minTrailing: Int
    private val endsMarker: Char

    // internal hyphen - separate frm external, display hyphen
    private val exceptions: MutableMap<String, Array<String>> = HashMap()
    private val patterns: MutableMap<String, IntArray> = HashMap()

    init {
        loadPatterns(language, context)
        minLength = 5
        minLeading = 2
        minTrailing = 3
        endsMarker = '.'
    }
}