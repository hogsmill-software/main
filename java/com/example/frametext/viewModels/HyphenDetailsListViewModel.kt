package com.example.frametext.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.frametext.globalObjects.HyphenDetails

class HyphenDetailsListViewModel : ViewModel() {
    private val selectedItems: MutableLiveData<ArrayList<HyphenDetails>> =
        MutableLiveData<ArrayList<HyphenDetails>>()

    fun selectItem(item: ArrayList<HyphenDetails>) {
        selectedItems.value = item
    }

  //  fun getSelectedItem(): LiveData<ArrayList<HyphenDetails>> {
 //       return selectedItem
   // }

    val selectedItem: LiveData<ArrayList<HyphenDetails>>
        get() = selectedItems
}