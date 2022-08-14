package com.example.frametext.userControls.colorPicker

interface ColorObservable {
    fun subscribe(observer: ColorObserver)
    fun unsubscribe(observer: ColorObserver?)
    val color: Int
}