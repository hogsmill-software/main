package com.example.frametext.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserFriendlyFontFamilyFontFamilyMapViewModel : ViewModel() {
    private val selectedItem = MutableLiveData<HashMap<String, String>>()

    fun selectItem(item: HashMap<String, String>) {
        selectedItem.value = item
    }

    fun getSelectedItem(): LiveData<HashMap<String, String>> {
        return selectedItem
    }
}
