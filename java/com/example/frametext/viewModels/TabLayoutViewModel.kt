package com.example.frametext.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.tabs.TabLayout

class TabLayoutViewModel : ViewModel() {
    private val selectedItem = MutableLiveData<TabLayout>()
    fun selectItem(item: TabLayout) {
        selectedItem.value = item
    }

    fun getSelectedItem(): LiveData<TabLayout> {
        return selectedItem
    }
}
