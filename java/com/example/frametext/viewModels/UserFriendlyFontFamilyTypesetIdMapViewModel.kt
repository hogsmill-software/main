package com.example.frametext.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class UserFriendlyFontFamilyTypesetIdMapViewModel : ViewModel() {
    private val selectedItem = MutableLiveData<HashMap<String, Int>>()

    fun selectItem(item: HashMap<String, Int>) {
        selectedItem.value = item
    }

    fun getSelectedItem(): LiveData<HashMap<String, Int>> {
        return selectedItem
    }
}
