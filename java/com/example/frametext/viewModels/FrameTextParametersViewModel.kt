package com.example.frametext.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.frametext.globalObjects.FrameTextParameters

class FrameTextParametersViewModel : ViewModel() {
    private val selectedItem: MutableLiveData<FrameTextParameters> =
        MutableLiveData<FrameTextParameters>()

    fun selectItem(item: FrameTextParameters) {
        selectedItem.value = item
    }

    fun getSelectedItem(): LiveData<FrameTextParameters> {
        return selectedItem
    }
}