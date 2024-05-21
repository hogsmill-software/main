package com.example.frametext.engine

import android.content.Context
import android.graphics.*
import androidx.core.content.res.ResourcesCompat
import com.example.frametext.engine.mainSizes.MainSizes
import com.example.frametext.engine.textBoundaries.TextBoundaries
import com.example.frametext.enums.MainShapeType
import com.example.frametext.hyphens.Hyphenator
import com.example.frametext.shapes.edge.EdgeShapeDetails
import java.util.*
import kotlin.math.roundToInt

internal class DrawText(
    private val canvas: Canvas,
    mainSizes: MainSizes,
    sd: EdgeShapeDetails,
    private val tfd: TextFormattingDetails,
    mainShapeType: MainShapeType?,
    context: Context?) {
    private val paint: Paint
    private val tb: TextBoundaries
    private var hyphenator: Hyphenator? = null
    private val hyphen = '-'
    private val hyphenWidth: Float
    private var rectLst: List<TextRectDetails> = ArrayList()
    private var lastWord: String? = null
    private var charGap: Float = 0f
    private var charGapChange: Float = 1f
    fun computeTextSpaceAvailable(): Int {
        rectLst = tb.computeTextRectangles()
        var retVal = 0
        for (txtRectDetails in rectLst) {
            retVal += txtRectDetails.boundingRect.width()
        }
        return retVal
    }

    private fun handleWordInProgressNoPunctuation(
        wordInProgressNoPunctuation: StringBuilder,
        punctuations: StringBuilder,
        brokenWordsList: MutableList<String>
    ) {
        if (wordInProgressNoPunctuation.isNotEmpty()) {
            val brokenWords: Array<String> =
                hyphenator?.hyphenateWord(wordInProgressNoPunctuation.toString()) ?: arrayOf(wordInProgressNoPunctuation.toString())
            if (brokenWordsList.size == 0) {
                brokenWords[0] = punctuations.toString() + brokenWords[0]
                brokenWordsList.addAll(brokenWords)
            } else {
                brokenWordsList[brokenWordsList.size - 1] =
                    brokenWordsList[brokenWordsList.size - 1] + punctuations.toString() + brokenWords[0]
                brokenWordsList.addAll(listOf(*brokenWords).subList(1, brokenWords.size))
            }
            punctuations.setLength(0)
            wordInProgressNoPunctuation.setLength(0)
        }
    }

    private fun getCharWidth(chr: Char): Float {
        val width: Float
        if (charWidthMap.containsKey(chr)) {
            width = charWidthMap[chr] ?: 0.0f
        }
        else {
            width = paint.measureText(chr.toString())
            charWidthMap[chr] = width
        }
        return width + charGap
    }

    // Computes bounding rectangles and text inside each of the rectangles
    fun computeTextPlacementDetails(reInitRectangles: Boolean = true) {
        var chr: Char
        val lineInProgress = StringBuilder()
        val wordInProgress = StringBuilder()
        var lineWidth = 0f // The total width of characters on line inside a bounding rectangle.
        var wordWidth = 0 // The total width of characters of a word.
        var noHyphenWord =
            false // This is set to true when we are not hyphenating a particular word.
        try {
            if (reInitRectangles) {
                rectLst = tb.computeTextRectangles()
            }
            val rectLstIterator = rectLst.iterator()
            if (rectLstIterator.hasNext()) {
                var txtRectDetails = rectLstIterator.next()
                var boundingRect: Rect = txtRectDetails.boundingRect
                if (!textInputDetails.isInitialized) {
                    textInputDetails.initialise(tfd.contentText)
                } else {
                    textInputDetails.resetAllTextFits()
                }
                for (lineDetIdx in 0 until textInputDetails.count()) {
                    val txtLineDetails = textInputDetails.getLineDetails(lineDetIdx)
                    val data: String = txtLineDetails.line
                    if (data.isNotEmpty()) {
                        // We need the last word to know if optimized size for placing all text.
                        // If use just last text in rectangle, this text may already have been hyphenated so no good...
                        val dataTrim = data.trim { it <= ' ' }
                        // Safe if no ' ' in string. lastIndexOf returns -1 so lastWord = substring(0) - same as dataTrim - desired effect
                        lastWord = dataTrim.substring(dataTrim.lastIndexOf(" ") + 1)
                        lineWidth = 0f
                        txtLineDetails.allTextFits = false
                    }
                    var breakLoop = false
                    var emojiLen = 0
                    var emojiPos = -2
                    var emojiShiftedNextRectangle = false
                    var i = 0
                    while (i < data.length) {

                        // Have to skip emoji characters already processed
                        if (i == emojiPos + 1) {
                            i += emojiLen - 1

                            // Case emoji is last character of line.
                            if (i >= data.length) {
                                break
                            }
                        }
                        chr = data[i]
                        val isEmoji = EmojiHelper.isCharEmojiAtPos(data, i)
                        var charWidth: Float
                        if (isEmoji) {
                            emojiPos = i
                            emojiLen = EmojiHelper.emojiLengthAtPos(data, i)
                            charWidth = EmojiHelper.getEmojiWidth(data, i, emojiLen, paint)
                        } else {
                            charWidth = getCharWidth(chr)
                        }
                        // Handling of the { } and {-} here...
                        if (chr == '{' && i < data.length - 2) {
                            var handlerRet = -1
                            if (data[i + 1] == ' ') {
                                // Handling of "{ }" below. (non-break space)
                                if (data[i + 2] == '}') {
                                    wordInProgress.append(NON_BREAK_SPACE)
                                    wordWidth += getCharWidth(NON_BREAK_SPACE).toInt()
                                    handlerRet = i + 2
                                } else if (data[i + 2] == ' ') {
                                    // in order to allow user to enter "{ }", all user has to do is enter "{  }" (2 spaces and one is removed)
                                    wordInProgress.append(chr)
                                    wordWidth += charWidth.toInt()
                                    handlerRet = i + 1
                                }
                                // else invalid entry warning?
                            }
                            if (data[i + 1] == 'x') {
                                // Handling of "{x}" below. (no hyphenation for word following)
                                if (data[i + 2] == '}') {
                                    noHyphenWord = true
                                    handlerRet = i + 2
                                } else if (data[i + 2] == 'x') {
                                    wordInProgress.append(chr)
                                    wordWidth += charWidth.toInt()
                                    handlerRet = i + 1
                                }
                            } else if (data[i + 1] == '-') {
                                if (data[i + 2] == '}') {
                                    // We only need to perform computations below once so store result in a map
                                    if (usrExceptionMap.containsKey(i)) {
                                        val usrExceptionDetails = usrExceptionMap[i]
                                        wordInProgress.setLength(0)

                                        usrExceptionDetails?.let {
                                            wordInProgress.append(it.word)
                                            wordWidth = it.wordWidth
                                            handlerRet = it.handlerRet
                                        }
                                    } else {
                                        // Normally, the start of new exception we want for hyphenation is wordInProgress
                                        // Strictly speaking, that is not always correct. User might have added a non space break prefixing word.
                                        // User might also have entered inverted exclamation mark or inverted question mark (used in Spanish)
                                        // We will handle those possibilities...
                                        var nbsPos = -1
                                        for (idx in wordInProgress.indices) {
                                            if (!Character.isJavaIdentifierPart(
                                                    wordInProgress[idx]
                                                )
                                            ) {
                                                nbsPos = idx
                                            }
                                        }
                                        val exception = ArrayList<String>()
                                        if (nbsPos == -1) {
                                            exception.add(wordInProgress.toString())
                                        } else {
                                            exception.add(wordInProgress.substring(nbsPos + 1)) // test case last chr nbs
                                        }
                                        var j = i + 3
                                        val wordFragment = StringBuilder()
                                        while (j < data.length) {
                                            val currChr = data[j]
                                            if (!Character.isJavaIdentifierPart(currChr) && currChr != '{') break
                                            if (currChr == '{' && j < data.length - 2 && data[j + 1] == '-' && data[j + 2] == '}') {
                                                exception.add(wordFragment.toString())
                                                wordFragment.setLength(0)
                                                j += 2
                                            } else {
                                                wordInProgress.append(currChr)
                                                wordWidth += getCharWidth(currChr).toInt()
                                                wordFragment.append(currChr)
                                            }
                                            ++j
                                        }
                                        exception.add(wordFragment.toString())
                                        handlerRet = j - 1
                                        hyphenator?.addException(exception.toTypedArray())
                                        usrExceptionMap[i] = UserExceptionDetails(
                                            wordInProgress.toString(),
                                            wordWidth,
                                            handlerRet
                                        )
                                    }
                                } else if (data[i + 2] == '-') {
                                    // in order to allow user to enter "{-}", all user has to do is enter "{--}" (2 hyphens and one is removed)
                                    wordInProgress.append(chr)
                                    wordWidth += charWidth.toInt()
                                    handlerRet = i + 1
                                }
                            }
                            if (handlerRet != -1) {
                                i = handlerRet // move so many characters ahead
                                i++
                                continue
                            }
                        }
                        if (chr != ' ' && !isEmoji) {
                            wordInProgress.append(chr)
                            wordWidth += charWidth.toInt()
                        }
                        if (chr == ' ' || i == data.length - 1 || isEmoji) {
                            // In French, non-breaking space before exclamation, question marks or colon
                            if (chr == ' ' && i != data.length - 1 && (data[i + 1] == '!' || data[i + 1] == '?' || data[i + 1] == ':')) {
                                wordInProgress.append(NON_BREAK_SPACE)
                                wordWidth += charWidth.toInt()
                                i++
                                continue
                            }
                            if (lineWidth + wordWidth <= boundingRect.width()) {
                                lineInProgress.append(wordInProgress)
                                lineWidth += wordWidth.toFloat()
                                wordInProgress.setLength(0)
                                wordWidth = 0
                                if (chr == ' ' && lineWidth + charWidth <= boundingRect.width()) {
                                    lineInProgress.append(chr)
                                    lineWidth += charWidth
                                }
                                if (isEmoji) {
                                    if (lineWidth + charWidth <= boundingRect.width()) {
                                        lineInProgress.append(chr)
                                        for (idx in 1 until emojiLen) {
                                            lineInProgress.append(data[i + idx])
                                        }
                                        lineWidth += charWidth
                                    } else {
                                        wordInProgress.append(chr)
                                        for (idx in 1 until emojiLen) {
                                            wordInProgress.append(data[i + idx])
                                        }
                                        wordWidth += charWidth.toInt()
                                    }
                                }
                                if (i == data.length - 1) {
                                    txtLineDetails.allTextFits = true
                                }
                            } else {
                                if (isEmoji) {
                                    i-- // We are changing bounding rectangle, so we have to go back so we re-process the emoji we have just
                                    // done on next bounding box and we add word that didn't fit into next bounding rectangle that way..
                                    if (rectLstIterator.hasNext()) {
                                        txtRectDetails.text = lineInProgress.toString()
                                        txtRectDetails.textWidth = lineWidth.toInt()
                                        lineInProgress.setLength(0)
                                        lineWidth = 0f
                                        txtRectDetails = rectLstIterator.next()
                                        boundingRect = txtRectDetails.boundingRect
                                        // We are calling rectLstIterator.next(). Be careful or have an empty blank line.
                                        emojiShiftedNextRectangle = true
                                    } else {
                                        break
                                    }
                                }
                                if (tfd.hyphenateText && !noHyphenWord) {
                                    // wordInProgress may have punctuation marks.
                                    // No hyphenation can take place around these punctuation marks.
                                    // I have made that assumption in calculations below.
                                    // Finish punctuation marks may be multiple such as ?! or ...
                                    // I have handled inverted Spanish exclamation and question marks prefixing a word.
                                    // You can have apostrophe in middle of wordInProgress - example: "isn't"
                                    val wordInProgressNoPunctuation = StringBuilder()
                                    val punctuations = StringBuilder()
                                    val brokenWordsList: MutableList<String> = ArrayList()
                                    for (idx in wordInProgress.indices) {
                                        if (Character.isJavaIdentifierPart(wordInProgress[idx])) {
                                            wordInProgressNoPunctuation.append(wordInProgress[idx])
                                        } else {
                                            handleWordInProgressNoPunctuation(
                                                wordInProgressNoPunctuation,
                                                punctuations,
                                                brokenWordsList
                                            )
                                            punctuations.append(wordInProgress[idx])
                                        }
                                    }
                                    // Case space immediately after wordInProgressNoPunctuation
                                    handleWordInProgressNoPunctuation(
                                        wordInProgressNoPunctuation,
                                        punctuations,
                                        brokenWordsList
                                    )

                                    // Case punctuation marks after wordInProgressNoPunctuation: simply append these
                                    if (punctuations.isNotEmpty()) {
                                        if (brokenWordsList.size > 0) {
                                            brokenWordsList[brokenWordsList.size - 1] =
                                                brokenWordsList[brokenWordsList.size - 1] + punctuations
                                        } else {
                                            brokenWordsList.add(punctuations.toString())
                                        }
                                    }
                                    breakLoop = false
                                    for (idx in brokenWordsList.indices) {
                                        val str = brokenWordsList[idx]
                                        //	int brokenWordWidth = g2d.getFontMetrics().charsWidth(str.toCharArray(), 0, str.length());
                                        val brokenWordWidth = paint.measureText(str)
                                        val requiredWidth =
                                            if (idx < brokenWordsList.size - 1) brokenWordWidth + hyphenWidth else brokenWordWidth
                                        if (lineWidth + requiredWidth <= boundingRect.width()) {
                                            lineInProgress.append(str)
                                            lineWidth += brokenWordWidth
                                        } else {
                                            if (idx > 0) {
                                                lineInProgress.append(hyphen)
                                                lineWidth += hyphenWidth
                                            }
                                            txtRectDetails.text = lineInProgress.toString()
                                            txtRectDetails.textWidth = lineWidth.toInt()

                                            // BEWARE: not enough space in previous rectangle does not necessarily mean there will not be enough in any of next rectangles.
                                            var badFit = true
                                            while (rectLstIterator.hasNext() && badFit) {
                                                txtRectDetails = rectLstIterator.next()
                                                boundingRect = txtRectDetails.boundingRect

                                                // make sure not last. Otherwise mustn't affix hyphen width
                                                if (requiredWidth <= boundingRect.width()) {
                                                    badFit = false
                                                }
                                            }
                                            // badFit is important. Otherwise we exit loop which results in all text fits = false when all text could fit in.
                                            if (!rectLstIterator.hasNext() && badFit) {
                                                breakLoop = true
                                                break
                                            }
                                            lineInProgress.setLength(0)
                                            lineInProgress.append(str)
                                            lineWidth = brokenWordWidth
                                        }
                                    }
                                    // We have processed word in progress, so reset this...
                                    wordInProgress.setLength(0)
                                    wordWidth = 0
                                    if (breakLoop) {
                                        break
                                    } else if (i == data.length - 1) {
                                        txtLineDetails.allTextFits = true
                                    }
                                } else {
                                    txtRectDetails.text = lineInProgress.toString()
                                    txtRectDetails.textWidth = lineWidth.toInt()
                                    lineInProgress.setLength(0)
                                    lineWidth = 0f

                                    // BEWARE: not enough space in this rectangle does not necessarily mean there will not be enough in next rectangle.
                                    // Rectangles start by getting wider
                                    var badFit = true
                                    //	int currentWidth = txtRectDetails.getBoundingRect().width;
                                    // Protection so don't have a blank empty line. See Tom Ato sample which ends with tomato emoji. Use diamond main shape
                                    if (emojiShiftedNextRectangle) {
                                        boundingRect = txtRectDetails.boundingRect
                                        if (wordWidth <= boundingRect.width()) {
                                            badFit = false
                                        }
                                    }

                                    while (rectLstIterator.hasNext() && badFit) {
                                        txtRectDetails = rectLstIterator.next()
                                        boundingRect = txtRectDetails.boundingRect
                                        if (wordWidth <= boundingRect.width()) {
                                            badFit = false
                                        }

                                        //	currentWidth = txtRectDetails.getBoundingRect().width;
                                    }
                                    if (!badFit && i == data.length - 1) {
                                        txtLineDetails.allTextFits = true
                                    }
                                    if (!rectLstIterator.hasNext()) {
                                        breakLoop = true
                                        break
                                    }
                                }

                                if (emojiShiftedNextRectangle) {
                                    emojiShiftedNextRectangle = false
                                }

                                if (chr == ' ' && i != data.length - 1) {
                                    i-- // We are changing bounding rectangle, so we have to go back so we re-process the space we have just
                                    // done on next bounding box and we add word that didn't fit into next bounding rectangle that way..
                                }
                            }
                            noHyphenWord = false
                        }
                        i++
                    }
                    // Don't forget to add last line.
                    if (lineWidth + wordWidth <= boundingRect.width() && !breakLoop) {
                        // When Java splits data into lines using carriage return, it removes all end empty lines
                        // Kotlin doesn't do this, so have to check if empty otherwise erases last valid line.
                        if (data.isNotEmpty()) {
                            lineInProgress.append(wordInProgress)
                            lineWidth += wordWidth.toFloat()
                            txtRectDetails.text = lineInProgress.toString()
                            txtRectDetails.textWidth = lineWidth.toInt()
                            lineInProgress.setLength(0)
                            wordInProgress.setLength(0)
                            lineWidth = 0f
                            // here end of line set to true
                            txtRectDetails.setEndOfLine()
                            txtLineDetails.allTextFits = true
                        }
                    } else {
                        // We couldn't place last piece of text...
                        txtLineDetails.allTextFits = false
                    }

                    // have to make sure next rectangle is lower - top 2 rectangles have same y coordinate.
                    val boundingRectY = boundingRect.bottom
                    var yChanged = false
                    while (rectLstIterator.hasNext() && !yChanged) {
                        txtRectDetails = rectLstIterator.next()
                        boundingRect = txtRectDetails.boundingRect
                        if (boundingRect.bottom != boundingRectY) {
                            yChanged = true
                        }
                    }
                }
            }
        }
        catch (e: NoSuchElementException) {
            println("An error occurred with call to rectLstIterator.next() file.")
            e.printStackTrace()
        } catch (e: ConcurrentModificationException) {
            println("An error occurred with call to rectLstIterator.next() file.")
            e.printStackTrace()
        } catch (e: Exception) {
            println("An error occurred in method computeTextPlacementDetails().")
            e.printStackTrace()
        }
    }

    /*
    fun drawTextBoundingRectangles() {
        val col = paint.color
        paint.color = Color.BLUE
        rectLst?.let {
            for (txtRectDetails in it) {
                val txtBoundingRect = txtRectDetails.boundingRect
                canvas.drawRect(txtBoundingRect, paint)
            }
        }

        paint.color = col
    }
    */

    fun draw() {
        try {
            // Uncomment for testing purpose
            //drawTextBoundingRectangles();
            paint.color = tfd.txtColor
            for (txtRectDetails in rectLst) {
                val txtBoundingRect: Rect = txtRectDetails.boundingRect
                var txt: String? = txtRectDetails.text
                if (!txt.isNullOrEmpty()) {
                    if (tfd.optimizeSpacing) {
                        val availableWidth: Int = txtRectDetails.boundingRect.width()
                        txt = txt.trim { it <= ' ' }
                        // count characters with emoji not length anymore!
                        var usedWidth = 0
                        var interCharRatio = 0 // sum of all chars x2 except 1st and last x1
                        // getLineMetrics(str, beginIndex, limit, context).charWidth(txt.charAt(idx));
                        // With emoji, the number of characters is different from txt.length as an emoji
                        // can be a multiple combination of characters.
                        var numCharacters = 0
                        run {
                            var idx = 0
                            while (idx < txt.length) {
                                if (EmojiHelper.isCharEmojiAtPos(txt, idx)) {
                                    idx += EmojiHelper.emojiLengthAtPos(txt, idx) - 1
                                }
                                numCharacters++
                                idx++
                            }
                        }
                        val charsWidths = FloatArray(numCharacters)
                        var widthIdx = 0
                        var idx = 0
                        while (idx < txt.length) {
                            if (EmojiHelper.isCharEmojiAtPos(txt, idx)) {
                                val emojiLen = EmojiHelper.emojiLengthAtPos(txt, idx)
                                charsWidths[widthIdx] =
                                    EmojiHelper.getEmojiWidth(txt, idx, emojiLen, paint)
                                idx += emojiLen - 1
                            } else {
                                charsWidths[widthIdx] = getCharWidth(txt[idx])
                            }
                            usedWidth += (charsWidths[widthIdx]).toInt()
                            interCharRatio += if (idx == 0 || idx == txt.length - 1) {
                                (charsWidths[widthIdx]).toInt()
                            } else {
                                (2 * charsWidths[widthIdx]).toInt()
                            }
                            widthIdx++
                            idx++
                        }
                        var excessWidth = availableWidth - usedWidth
                        val excessUsedWidthRatio = excessWidth / usedWidth.toDouble()
                        var offset = 0.0
                        if (excessUsedWidthRatio > MAX_EXCESS_WIDTH) {
                            excessWidth = (usedWidth * MAX_EXCESS_WIDTH).toInt()
                        }
                        val chr = CharArray(1)

                        //	 for (int idx = 0; idx < txt.length(); idx++) {
                        var idxInTxt = 0
                        for (characterIdx in 0 until numCharacters) {
                            if (characterIdx > 0) {
                                offset += charsWidths[characterIdx] * excessWidth / interCharRatio.toDouble()
                            }
                            if (EmojiHelper.isCharEmojiAtPos(txt, idxInTxt)) {
                                val emojiLen = EmojiHelper.emojiLengthAtPos(txt, idxInTxt)
                                val str = txt.substring(idxInTxt, idxInTxt + emojiLen)
                                canvas.drawText(
                                    str,
                                    (txtBoundingRect.left + offset.roundToInt()).toFloat(),
                                    txtBoundingRect.top.toFloat(),
                                    paint
                                )
                                idxInTxt += emojiLen
                            } else {
                                chr[0] = txt[idxInTxt]
                                canvas.drawText(
                                    chr[0].toString(),
                                    (txtBoundingRect.left + offset.roundToInt()).toFloat(),
                                    txtBoundingRect.top.toFloat(),
                                    paint
                                )
                                idxInTxt++
                            }
                            if (characterIdx < txt.length - 1) {
                                offset += charsWidths[characterIdx]
                                offset += charsWidths[characterIdx] * excessWidth / interCharRatio.toDouble()
                            }
                        }
                    } else {
                        canvas.drawText(
                            txt,
                            txtBoundingRect.left.toFloat(),
                            txtBoundingRect.top.toFloat(),
                            paint
                        )
                    }
                }
            }
        } catch (e: Exception) {
            println("An error occurred in method draw().")
            e.printStackTrace()
        }
    }

    // Returns true if:
    // - All rectangles optimised.
    // - Last rectangles are empty but last word (no hyphenation) or last hyphenated word fragment are wider than these rectangle.
    // Otherwise returns false.
    fun sizeOptimized(): Boolean {
        var optimized = false
        val li = rectLst.listIterator(rectLst.size)
        var lastRectWidth = 0
        var previousRectWidth: Int
        var lastWordSegmentWidth = 0f
        if (tfd.hyphenateText) {
            lastWord?.let {
                val brokenWords: Array<String> =
                    hyphenator?.hyphenateWord(it) ?: arrayOf(it)
                val lastWordSegment = "-" + brokenWords[brokenWords.size - 1]
                //lastWordSegmentWidth = g2d.getFontMetrics().charsWidth(lastWordSegment.toCharArray(), 0, lastWordSegment.length());
                lastWordSegmentWidth = paint.measureText(lastWordSegment)
            }
        }
        if (li.hasPrevious()) {
            var trd = li.previous()
            if (trd.textWidth > 0) {
                // All rectangles used...
                optimized = true
            } else {
                while (li.hasPrevious()) {
                    previousRectWidth = lastRectWidth
                    lastRectWidth = trd.boundingRect.width()
                    if (previousRectWidth > lastRectWidth) {
                        // Heart is getting narrower here - useless processing any further.
                        break
                    }
                    trd = li.previous()
                    if (trd.textWidth > 0) {
                        if (tfd.hyphenateText) {
                            if (lastWordSegmentWidth < previousRectWidth) {
                                //		optimized = false;
                                break
                            }
                        } else {
                            // If the last word in rectangle is wider than rectangle below, then it is optimized.
                            val lastWordInRect: String =
                                trd.text.substring(trd.text.lastIndexOf(" ") + 1)

                            val lastWordInRectWidth = paint.measureText(lastWordInRect)
                            if (lastWordInRectWidth > lastRectWidth) {
                                optimized = true
                                break
                            }
                        }
                    }
                }
            }
        }
        return optimized
    }

    fun doesAllTextFit(): Boolean {
        return textInputDetails.allTextFits()
    }

    fun resetTextInputDetails() {
        textInputDetails.reset()
    }

    fun remUnUsedRectangles(): Int {
        // Computes remaining unused/empty rectangle.
        var txt = rectLst.last().text
        var idxRev = rectLst.size - 1
        var unUsedRectangles = 0

        while (txt == "" && --idxRev >= 0) {
            txt = rectLst[idxRev].text
            unUsedRectangles++
        }
        return unUsedRectangles
    }

    fun incrementCharGap() {
        charGap += charGapChange
    }

    fun decrementCharGap() {
        charGap -= charGapChange
    }

    fun reduceCharGapChange() {
        charGapChange /= 2f
    }

    fun charGapChangeMinThreshold(): Boolean {
        return charGapChange < 0.0625
    }

    fun clearTextFromRectangles() {
        rectLst.forEach {
            it.text = ""
            it.textWidth = 0
        }
    }

    companion object {
        private val HyphenatorLangMap: HashMap<String, Hyphenator?> = HashMap<String, Hyphenator?>()

        // Below must be reset and reloaded if text content changes, as must exceptions in HyphenatorLangMap above.
        private val usrExceptionMap = HashMap<Int, UserExceptionDetails>()
        private val charWidthMap = HashMap<Char, Float>()
        private val textInputDetails = TextInputDetails()
        private const val NON_BREAK_SPACE = 0xA0.toChar()
        private const val MAX_EXCESS_WIDTH = 0.5

        fun onFontChanged() {
            usrExceptionMap.clear()
            charWidthMap.clear()
        }
    }

    init {
        tfd.hyphenPatternLan?.let { hyphenPatternLanIt ->
            if (HyphenatorLangMap.containsKey(hyphenPatternLanIt)) {
                hyphenator = HyphenatorLangMap[hyphenPatternLanIt]
            } else {
                hyphenator = context?.let { Hyphenator(hyphenPatternLanIt, it) }
                HyphenatorLangMap[hyphenPatternLanIt] = hyphenator
            }
        }
        
        paint = Paint()

        if (tfd.fontFamily != "") {
            val tf = Typeface.create(tfd.fontFamily, tfd.fontStyle)
            paint.typeface = tf
        }
        else { // typeFace should be set to a value
            context?.let { paint.typeface = ResourcesCompat.getFont(it, tfd.typeFaceId) }
        }

        paint.textSize = 150f
        hyphenWidth = getCharWidth(hyphen)
        tb = ObjectFromShapeType.getTextBoundariesFromShapeType(
            mainShapeType, paint, mainSizes, sd,
            tfd
        )
    }
}

