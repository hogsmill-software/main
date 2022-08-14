package com.example.frametext.viewModels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.frametext.fragments.FrameTextImageFragment

class FrameTextBitmapViewModel : ViewModel() {
    private val selectedItem = MutableLiveData<Bitmap>()
    fun selectItem(item: Bitmap) {
        selectedItem.value = item
    }

    fun getSelectedItem(): LiveData<Bitmap> {
        return selectedItem
    }

    private val selectedImageFragment: MutableLiveData<FrameTextImageFragment> =
        MutableLiveData<FrameTextImageFragment>()

    fun selectImageFragment(item: FrameTextImageFragment) {
        selectedImageFragment.value = item
    }

    fun getSelectedImageFragment(): LiveData<FrameTextImageFragment> {
        return selectedImageFragment
    }
}