package com.example.frametext.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.frametext.R
import com.example.frametext.billing.SKU_MORE_EMOJIS
import com.example.frametext.billing.SKU_MORE_SYMBOLS
import com.example.frametext.billing.StoreManager
import com.example.frametext.databinding.ListNewFeatureItemsBinding
import com.example.frametext.fragments.NewFeaturesFragment
import com.example.frametext.helpers.Utilities
import com.example.frametext.userControls.AlertPopupOK
import com.example.frametext.viewModels.NewFeaturesViewModel

class NewFeatureListAdapter internal constructor(
    var context: Context,
    private val newFeaturesViewModel: NewFeaturesViewModel,
    private val newFeaturesFragment: NewFeaturesFragment,
    private var storeManager: StoreManager,
    private var activity: Activity
) :
    RecyclerView.Adapter<NewFeatureListAdapter.ViewHolder>() {
    private var sKUList: ArrayList<String>? = storeManager.knownInappSKUs.toCollection(ArrayList())
    private var closePopupBtn: Button? = null
    private var popupWindow: PopupWindow? = null
    private var linearLayoutNewFeatures: LinearLayout? = null
    private var infoPopupOpen: Boolean = false

    inner class ViewHolder(private val binding: ListNewFeatureItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var infoButton: AppCompatButton =
            binding.root.rootView.findViewById<View>(R.id.infoButton) as AppCompatButton
        var buyPurchasedButton: AppCompatButton =
            binding.root.rootView.findViewById<View>(R.id.buyPurchasedButton) as AppCompatButton

        fun bind(
            item: String,
            newFeaturesViewModel: NewFeaturesViewModel,
            newFeaturesFragment: NewFeaturesFragment
        ) {
            val infoTitle: Int = when (item) {
                SKU_MORE_EMOJIS -> R.string.title_more_emojis
                SKU_MORE_SYMBOLS -> R.string.title_more_symbols
                else -> 0
            }
            binding.featureName.text = context.resources.getString(infoTitle)

            newFeaturesViewModel.isPurchased(item).observe(
                newFeaturesFragment.viewLifecycleOwner
            ) {
                newFeaturesViewModel.canBuySku(item)
                    .observe(
                        newFeaturesFragment.viewLifecycleOwner
                    ) { canPurchase ->
                        if (canPurchase) {
                            setButtonToBuyStatus(binding.buyPurchasedButton)
                        } else {
                            // just purchased....
                            newFeaturesViewModel.isPurchased(item).observe(
                                newFeaturesFragment.viewLifecycleOwner
                            ) { isPurchased ->
                                if (isPurchased) {
                                    setButtonToPurchasedStatus(binding.buyPurchasedButton)
                                } else {
                                    setButtonToUnknownStatus(binding.buyPurchasedButton)
                                }
                            }
                        }
                    }
            }

            binding.lifecycleOwner = newFeaturesFragment
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListNewFeatureItemsBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        sKUList?.let { holder.bind(it[position], this.newFeaturesViewModel, newFeaturesFragment) }

        holder.infoButton.setOnClickListener {
            showInfo(position)
        }

        holder.buyPurchasedButton.setOnClickListener {
            buySKU(position)
        }
    }

    override fun getItemCount(): Int {
        return sKUList?.size ?: 0
    }

    private fun showInfo(position: Int) {
        if (!infoPopupOpen) {
            infoPopupOpen = true

            val sku = sKUList?.get(position)
            val layoutInflater =
                activity.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val customView = layoutInflater.inflate(
                R.layout.info_popup,
                newFeaturesFragment.view as ViewGroup,
                false
            )

            // Display description here
            val description = customView.findViewById<TextView>(R.id.infoDescription)
            val infoDescription: Int = when (sku) {
                SKU_MORE_EMOJIS -> R.string.description_emojis
                SKU_MORE_SYMBOLS -> R.string.description_symbols_and_colours
                else -> 0
            }
            sku?.let {
                description.text = context.resources.getString(infoDescription)

                // Hard coded so get a description string even if billing server not connected.
                // Getting from billing server gives dreadful layout anyway, so commented code below:
                /*newFeaturesViewModel.getSkuDetails(it).description?.observe(newFeaturesFragment.viewLifecycleOwner,
                    {
                        description.text = it
                    })*/
            }

            // Display image
            val img = customView.findViewById<ImageView>(R.id.infoInAppImage)
            NewFeaturesViewModel.sKUToResourceIdMap[sku]?.let { it1 -> img.setImageResource(it1) }

            // Display price here
            sku?.let { skuIt ->
                newFeaturesViewModel.getSkuDetails(skuIt).price.observe(newFeaturesFragment.viewLifecycleOwner) {
                    val price = customView.findViewById<TextView>(R.id.infoPrice)
                    price.text = context.resources.getString(R.string.price, it)
                }
            }
            // Show popup
            popupWindow = PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            linearLayoutNewFeatures =
                newFeaturesFragment.view?.findViewById<View>(R.id.linearLayoutNewFeatures) as LinearLayout
            popupWindow?.showAtLocation(linearLayoutNewFeatures, Gravity.CENTER, 0, 0)
            // Handle close button
            closePopupBtn = customView.findViewById<View>(R.id.closePopupBtn) as Button
            closePopupBtn?.setOnClickListener {
                popupWindow?.dismiss()
                infoPopupOpen = false
            }
        }
    }

    private fun buySKU(position: Int) {
        var isPurchased: Boolean
        val sku = sKUList?.get(position)
        var count = 0
        sku?.let { skuIt ->
            newFeaturesViewModel.isPurchased(skuIt).observe(newFeaturesFragment.viewLifecycleOwner) {
                if (count > 0) {
                    return@observe // Don't want this loop executed more than once
                }
                count++
                isPurchased = it
                if (isPurchased) {
                    // Show popup already purchased
                    newFeaturesFragment.view?.let { it1 ->
                        AlertPopupOK(context.resources.getString(R.string.already_purchased_), context.resources.getString(R.string.already_purchased_)).show(
                            it1, context)
                    }
                } else {
                    storeManager.launchBillingFlow(activity, skuIt)
                }
            }
        }
    }

    fun setButtonToBuyStatus(buyPurchasedButton: AppCompatButton) {
        buyPurchasedButton.visibility = View.VISIBLE
        buyPurchasedButton.setBackgroundColor(ContextCompat.getColor(context, Utilities.getPinkMagentaColorId(context)))
        buyPurchasedButton.text = context.resources.getString(R.string.buy)
        buyPurchasedButton.setTypeface(null, Typeface.NORMAL)
    }

    fun setButtonToPurchasedStatus(buyPurchasedButton: AppCompatButton) {
        buyPurchasedButton.visibility = View.VISIBLE
        buyPurchasedButton.setBackgroundColor(ContextCompat.getColor(context, R.color.navyBlue))
        buyPurchasedButton.text = context.resources.getString(R.string.already_purchased)
        buyPurchasedButton.setTypeface(null, Typeface.ITALIC)
    }

    fun setButtonToUnknownStatus(buyPurchasedButton: AppCompatButton) {
        buyPurchasedButton.visibility = View.GONE
    }

    fun closeInfoPopup() {
        if (infoPopupOpen) {
            popupWindow?.dismiss()
            infoPopupOpen = false
        }
    }
}