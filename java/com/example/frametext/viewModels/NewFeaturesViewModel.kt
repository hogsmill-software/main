package com.example.frametext.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.frametext.R
import com.example.frametext.billing.SKU_MORE_EMOJIS
import com.example.frametext.billing.SKU_MORE_SYMBOLS
import com.example.frametext.billing.StoreManager
import java.util.HashMap

class NewFeaturesViewModel(private val storeManager: StoreManager) : ViewModel() {
    companion object {
        val sKUToResourceIdMap: MutableMap<String, Int> = HashMap()

        init {
            sKUToResourceIdMap[SKU_MORE_EMOJIS] = R.drawable.sku_more_emojis
            sKUToResourceIdMap[SKU_MORE_SYMBOLS] = R.drawable.sku_more_symbols
        }
    }

    class SkuDetails internal constructor(sku: String, storeManager: StoreManager) {
        val title = storeManager.getSkuTitle(sku).asLiveData()
        //  val description = storeManager.getSkuDescription(sku).asLiveData()
        val price = storeManager.getSkuPrice(sku).asLiveData()
    }

    fun getSkuDetails(sku: String): SkuDetails {
        return SkuDetails(sku, storeManager)
    }

    fun canBuySku(sku: String): LiveData<Boolean> {
        return storeManager.canPurchase(sku).asLiveData()
    }

    fun isPurchased(sku: String): LiveData<Boolean> {
        return storeManager.isPurchased(sku).asLiveData()
    }

    class NewFeaturesViewModelFactory(private val storeManager: StoreManager) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewFeaturesViewModel::class.java)) {
                return modelClass.cast(NewFeaturesViewModel(storeManager)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}