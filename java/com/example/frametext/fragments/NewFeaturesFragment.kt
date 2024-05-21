package com.example.frametext.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frametext.FrameTextApplication
import com.example.frametext.R
import com.example.frametext.adapters.NewFeatureListAdapter
import com.example.frametext.databinding.FragmentNewFeaturesBinding
import com.example.frametext.viewModels.NewFeaturesViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

class NewFeaturesFragment : Fragment() {

    private var fragmentActivityContext: FragmentActivity? = null
    private var newFeaturesViewModel: NewFeaturesViewModel? = null
    private var newFeatureListAdapter: NewFeatureListAdapter? = null
    private var binding: FragmentNewFeaturesBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentActivityContext = context as FragmentActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_new_features, container, false)
        binding = FragmentNewFeaturesBinding.inflate(layoutInflater)
        container?.removeAllViews()
        val view = binding!!.root

        val button = view.findViewById<View>(R.id.backButton)
        button.setOnClickListener {
            navigateToSettingsFragment()
            newFeatureListAdapter?.closeInfoPopup()
        }

        return view
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val newFeaturesViewModelFactory: NewFeaturesViewModel.NewFeaturesViewModelFactory =
            NewFeaturesViewModel.NewFeaturesViewModelFactory(
                (requireActivity().application as FrameTextApplication).appContainer.storeManager
            )
        ViewModelProvider(this, newFeaturesViewModelFactory)[NewFeaturesViewModel::class.java].also { newFeaturesViewModel = it }

        binding?.let {
            it.nfvm = newFeaturesViewModel
            it.inAppInventory.layoutManager = LinearLayoutManager(context)
        }

        activity?.let{
            val storeManager =
                (it.application as FrameTextApplication).appContainer.storeManager

            newFeaturesViewModel?.let{ newFeaturesViewModelIt ->
                newFeatureListAdapter = NewFeatureListAdapter(
                    requireContext(),
                    newFeaturesViewModelIt,
                    this,
                    storeManager,
                    it
                )
            }

            binding?.let { bindingIt ->
                bindingIt.inAppInventory.adapter = newFeatureListAdapter
            }
        }
    }

    private fun navigateToSettingsFragment() {
        val fragment: Fragment = SettingsFragment()

        fragmentActivityContext?.let {
            val fragmentManager: FragmentManager =
                it.supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.settings_frame, fragment)
                .setReorderingAllowed(true)
                .commit()
        }
    }
}