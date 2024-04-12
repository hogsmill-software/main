package com.example.frametext.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TypesetIdUserFriendlyFontFamilyMapViewModel : ViewModel()  {
    private val selectedItem = MutableLiveData<HashMap<Int, String>>()

    fun selectItem(item: HashMap<Int, String>) {
        selectedItem.value = item
    }

    fun getSelectedItem(): LiveData<HashMap<Int, String>> {
        return selectedItem
    }
}
