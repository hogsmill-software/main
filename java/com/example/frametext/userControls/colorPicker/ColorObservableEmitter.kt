package com.example.frametext.userControls.colorPicker

internal class ColorObservableEmitter : ColorObservable {
    private val observers: MutableList<ColorObserver> = ArrayList()
    override var color = 0
        private set

    override fun subscribe(observer: ColorObserver) {
        observers.add(observer)
    }

    override fun unsubscribe(observer: ColorObserver?) {
        if (observer == null) return
        observers.remove(observer)
    }

    fun onColor(color: Int, fromUser: Boolean, shouldPropagate: Boolean) {
        this.color = color
        for (observer in observers) {
            observer.onColor(color, fromUser, shouldPropagate)
        }
    }
}
