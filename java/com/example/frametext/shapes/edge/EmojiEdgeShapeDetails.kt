package com.example.frametext.shapes.edge

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface

class EmojiEdgeShapeDetails(private val emoji: String) : EdgeShapeDetails {
    override var centerX = 0f
        private set
    override var centerY = 0f
        private set
    override var width = 0f
        private set
    override var height = 0f
        private set

    private fun initialize() {
        val tf = Typeface.create("TimesRoman", Typeface.NORMAL)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.typeface = tf
        paint.textSize = 150f
        val rectHeart = Rect()
        paint.getTextBounds(emoji, 0, emoji.length, rectHeart)
        centerX = rectHeart.exactCenterX()
        // This looks a quickfix for heart shape. Investigate properly when time.
        centerY = rectHeart.exactCenterY() - 12 // have to add 12 or trespasses
        when (emoji) {
            "\uD83D\uDC8C️" -> {
                width = (rectHeart.width() * 0.89).toFloat()
                height = (rectHeart.height() * 0.63).toFloat()
            }
            "\uD83D\uDC93" -> {
                width = (rectHeart.width() * 0.95).toFloat()
                height = (rectHeart.height() * 0.93).toFloat()
            }
            "\uD83D\uDC95", "\uD83D\uDC9E", "\uD83D\uDC98", "\uD83D\uDD34", "\uD83D\uDFE0", "\uD83D\uDFE1", "\uD83D\uDFE2", "\uD83D\uDD35", "\uD83D\uDFE3", "⚫", "⚪", "\uD83D\uDFE4", "\uD83D\uDD36", "\uD83D\uDD37", "\uD83D\uDFE5", "\uD83D\uDFE7", "\uD83D\uDFE8", "\uD83D\uDFE9", "\uD83D\uDFE6", "\uD83D\uDFEA", "⬛", "⬜", "\uD83D\uDFEB", "\uD83D\uDCA5", "☀" -> {
                width = (rectHeart.width() * 0.89).toFloat()
                height = (rectHeart.height() * 0.95).toFloat()
            }
            "\uD83D\uDD3A", "\uD83D\uDD3B" -> {
                width = (rectHeart.width() * 0.48).toFloat()
                height = (rectHeart.height() * 0.46).toFloat()
            }
            "\uD83D\uDD38", "\uD83D\uDD39" -> {
                width = (rectHeart.width() * 0.34).toFloat()
                height = (rectHeart.height() * 0.36).toFloat()
            }
            "♠" -> {
                width = (rectHeart.width() * 0.80).toFloat()
                height = (rectHeart.height() * 0.95).toFloat()
            }
            "♣" -> {
                width = (rectHeart.width() * 0.86).toFloat()
                height = (rectHeart.height() * 0.95).toFloat()
            }
            "♦" -> {
                width = (rectHeart.width() * 0.65).toFloat()
                height = (rectHeart.height() * 0.95).toFloat()
            }
            "\uD83D\uDCA7" -> {
                width = (rectHeart.width() * 0.54).toFloat()
                height = (rectHeart.height() * 0.95).toFloat()
            }
            "\uD83E\uDE78" -> {
                width = (rectHeart.width() * 0.60).toFloat()
                height = (rectHeart.height() * 0.93).toFloat()
            }
            "⭐" -> {
                width = (rectHeart.width() * 0.89).toFloat()
                height = (rectHeart.height() * 0.93).toFloat()
            }
            "\uD83C\uDF1F" -> {
                width = (rectHeart.width() * 0.89).toFloat()
                height = (rectHeart.height() * 0.99).toFloat()
            }
            "✨" -> {
                width = (rectHeart.width() * 0.89).toFloat()
                height = (rectHeart.height() * 0.92).toFloat()
            }
            "\uD83D\uDD25" -> {
                width = (rectHeart.width() * 0.70).toFloat()
                height = (rectHeart.height() * 0.95).toFloat()
            }
            "\uD83D\uDC9D" -> {
                width = (rectHeart.width() * 1.0).toFloat()
                height = (rectHeart.height() * 0.87).toFloat()
                width = (rectHeart.width() * 0.89).toFloat()
                height = (rectHeart.height() * 0.87).toFloat()
            }
            else -> {
                width = (rectHeart.width() * 0.89).toFloat()
                height = (rectHeart.height() * 0.87).toFloat()
            }
        }
    }

    val verticalAdjustment: Float
        get() {
            when (emoji) {
                "\uD83D\uDC8C️" -> return 28.5f
                "\uD83D\uDC95" -> return 2f
                "\uD83D\uDC9E" -> return 0f
                "\uD83D\uDC93" -> return 0f
                "\uD83D\uDC98" -> return 0f
                "\uD83D\uDD34", "\uD83D\uDFE0", "\uD83D\uDFE1", "\uD83D\uDFE2", "\uD83D\uDD35", "\uD83D\uDFE3", "⚫", "⚪", "\uD83D\uDFE4" -> return 0f
                "\uD83D\uDD3A", "\uD83D\uDD3B" -> return 45f
                "\uD83D\uDD38", "\uD83D\uDD39" -> return 52f
                "\uD83D\uDD36", "\uD83D\uDD37", "\uD83D\uDFE5", "\uD83D\uDFE7", "\uD83D\uDFE8", "\uD83D\uDFE9", "\uD83D\uDFE6", "\uD83D\uDFEA", "⬛", "⬜", "\uD83D\uDFEB", "♠", "♣", "♦", "\uD83D\uDCA7" -> return 1f
                "\uD83E\uDE78" -> return 2f
                "⭐" -> return 4f
                "\uD83C\uDF1F" -> return (-5).toFloat()
                "✨" -> return 4f
                "\uD83D\uDCA5" -> return 2f
                "\uD83D\uDD25" -> return 0f
                "☀" -> return 0f
            }
            return 8f
        }
    val horizontalAdjustment: Float
        get() {
            when (emoji) {
                "\uD83D\uDC98" -> return (-10).toFloat()
                "\uD83D\uDC9D" -> return (-10).toFloat()
                "\uD83D\uDD34", "\uD83D\uDFE0", "\uD83D\uDFE1", "\uD83D\uDFE2", "\uD83D\uDD35", "\uD83D\uDFE3", "⚫", "⚪", "\uD83D\uDFE4" -> return (-10).toFloat()
                "\uD83D\uDD3A", "\uD83D\uDD3B" -> return (-47).toFloat()
                "\uD83D\uDD38", "\uD83D\uDD39" -> return (-60).toFloat()
                "\uD83D\uDD36", "\uD83D\uDD37", "\uD83D\uDFE5", "\uD83D\uDFE7", "\uD83D\uDFE8", "\uD83D\uDFE9", "\uD83D\uDFE6", "\uD83D\uDFEA", "⬛", "⬜", "\uD83D\uDFEB" -> return (-11).toFloat()
                "♠" -> return (-18).toFloat()
                "♣" -> return (-13).toFloat()
                "♦" -> return (-32).toFloat()
                "\uD83D\uDCA7" -> return (-44).toFloat()
                "\uD83E\uDE78" -> return (-37).toFloat()
                "⭐" -> return (-10).toFloat()
                "\uD83C\uDF1F" -> return (-10).toFloat()
                "✨" -> return (-10).toFloat()
                "\uD83D\uDCA5" -> return (-10).toFloat()
                "\uD83D\uDD25" -> return (-28).toFloat()
                "☀" -> return (-11).toFloat()
            }
            return (-8).toFloat()
        }

    /* Utility for testing placement of emojis.
      public void drawEmojiWithBoundaries(Canvas canvas, Paint paint) {
          // This code draws a rectangle and the bounding rect that should touch edges of heart.
          // Adjusted rect so heart reaches its sides
          paint.setColor(Color.BLUE);
   //       canvas.drawText(emoji, 150, 150, paint);
          canvas.drawRect((float)150  + centerX - width/2,
                  (float)150 + centerY - height/2,
                  150 + centerX + width/2,
                  150 + centerY + height/2, paint);
           canvas.drawText(emoji, 150, 150, paint);
      }
  */
    override fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        canvas.drawText(emoji, x, y, paint!!)
    }

    override val bottomAdjustment: Float
        get() = (-33).toFloat()

    init {
        initialize()
    }
}
