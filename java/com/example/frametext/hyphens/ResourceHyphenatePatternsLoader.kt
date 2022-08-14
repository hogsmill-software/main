package com.example.frametext.hyphens

import android.content.Context
import com.example.frametext.MainActivity
import java.io.FileInputStream
import java.util.*

class ResourceHyphenatePatternsLoader(private val hpl: String, private val context: Context) {
    private val patterns: MutableList<String> = ArrayList()
    private val exceptions: MutableList<String> = ArrayList()
    fun load() {
        try {
            val hyphenFileFolder: String = MainActivity.getHyphenFileFolder(context)
                ?: throw Exception("Hyphen folder not created.")
            val inputStream = FileInputStream(hyphenFileFolder + hpl)
            //	File file = new File(System.getenv(getEnvString())); Does not work for larger files like hyph-en-gb.tex!
            val scanner = Scanner(inputStream)
            var readPatterns = false
            var readHyphenation = false
            var reset = false
            while (scanner.hasNextLine()) {
                var data = scanner.nextLine()
                if (data.isNotEmpty() && data[0] != '%') {
                    if (data.contains("%")) {
                        data = data.substring(0, data.indexOf('%'))
                    }
                    data = data.trim { it <= ' ' }
                    if (data == "\\patterns{") {
                        readPatterns = true
                        continue
                    } else if (data == "\\hyphenation{") {
                        readHyphenation = true
                        continue
                    } else if (data.contains("}")) {
                        data = data.substring(0, data.indexOf('}'))
                        data = data.trim { it <= ' ' }
                        reset = true
                    } else if (data.contains("'")) {
                        continue
                    }
                    if (data.contains(" ")) {
                        val dataSplit = data.split(" ".toRegex()).toTypedArray()
                        for (str in dataSplit) {
                            addElement(str, readPatterns, readHyphenation)
                        }
                    } else {
                        addElement(data, readPatterns, readHyphenation)
                    }
                    if (reset) {
                        readPatterns = false
                        readHyphenation = false
                        reset = false
                    }
                }
            }
            scanner.close()
        } catch (e: Exception) {
            println("An error occurred.")
            e.printStackTrace()
        }
    }

    fun getPatterns(): List<String> {
        return patterns
    }

    fun getExceptions(): List<String> {
        return exceptions
    }

    private fun addElement(tag: String, readPatterns: Boolean, readHyphenation: Boolean) {
        if (readPatterns) {
            patterns.add(tag)
        } else if (readHyphenation) {
            exceptions.add(tag)
        }
    }
}
