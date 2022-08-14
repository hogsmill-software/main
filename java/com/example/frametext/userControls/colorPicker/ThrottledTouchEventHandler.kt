package com.example.frametext.userControls.colorPicker

import android.view.MotionEvent

internal class ThrottledTouchEventHandler private constructor(
    private val minInterval: Int,
    updatable: Updatable
) {
    private val updatable: Updatable?
    private var lastPassedEventTime: Long = 0

    constructor(updatable: Updatable) : this(Constants.EVENT_MIN_INTERVAL, updatable)

    fun onTouchEvent(event: MotionEvent?) {
        if (updatable == null) return
        val current = System.currentTimeMillis()
        if (current - lastPassedEventTime <= minInterval) {
            return
        }
        lastPassedEventTime = current
        updatable.update(event)
    }

    init {
        this.updatable = updatable
    }
}
