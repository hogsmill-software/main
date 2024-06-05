package com.example.frametext.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frametext.globalObjects.HyphenDetails
import com.example.frametext.R
import com.example.frametext.adapters.HyphenDetailsListAdapter
import com.example.frametext.viewModels.HyphenDetailsListViewModel
import com.example.frametext.viewModels.HyphenFilesListViewModel


class HyphenFilesFragment : Fragment() {
    private var hyphenDetailsList: ArrayList<HyphenDetails>? = null
    private var hyphenFilesRecyclerView: RecyclerView? = null
    private var hyphenFilesList: ArrayList<String>? = null
    private var fragmentActivityContext: FragmentActivity? = null
    private var hyphenDetailsListAdapter: HyphenDetailsListAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        container?.removeAllViews()

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_hyphen_files, container, false)

        val button = view.findViewById<View>(R.id.backButton)
        button.setOnClickListener { navigateToSettingsFragment() }

        hyphenFilesRecyclerView = view.findViewById(R.id.hyphenFilesRecyclerView)
        linearLayoutManager = LinearLayoutManager(context)
        hyphenFilesRecyclerView?.layoutManager = linearLayoutManager

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentActivityContext = context as FragmentActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val hyphenDetailsListViewModel: HyphenDetailsListViewModel =
            ViewModelProvider(requireActivity())[HyphenDetailsListViewModel::class.java]
        hyphenDetailsList = hyphenDetailsListViewModel.selectedItem.value
        val hyphenFilesListViewModel: HyphenFilesListViewModel =
            ViewModelProvider(requireActivity())[HyphenFilesListViewModel::class.java]
        hyphenFilesList = hyphenFilesListViewModel.selectedItem.value

        hyphenDetailsList?.let {
            val hd: Array<HyphenDetails> = it.toArray(arrayOfNulls(0))

            hyphenDetailsListAdapter = hyphenFilesList?.let { hyphenFilesListIt ->
                HyphenDetailsListAdapter(view.context, hd, hyphenFilesListIt)
            }
            hyphenFilesRecyclerView?.adapter = hyphenDetailsListAdapter

            // Add dividers
            val dividerItemDecoration = DividerItemDecoration(
                hyphenFilesRecyclerView?.context,
                linearLayoutManager.orientation
            )
            hyphenFilesRecyclerView?.addItemDecoration(dividerItemDecoration)
        }
    }

    private fun navigateToSettingsFragment() {
        fragmentActivityContext?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.settings_frame, SettingsFragment())
            ?.setReorderingAllowed(true)
            ?.commit()
    }
}