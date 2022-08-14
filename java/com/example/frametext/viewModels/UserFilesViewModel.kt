package com.example.frametext.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserFilesViewModel : ViewModel() {
    private val selectedItems = MutableLiveData<ArrayList<String>>()
    fun selectItems(items: ArrayList<String>) {
        selectedItems.value = items
    }

    val selectedItem: LiveData<ArrayList<String>>
        get() = selectedItems
}