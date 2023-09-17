package com.example.frametext.fragments

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.frametext.R
import com.example.frametext.engine.FrameTextException
import com.example.frametext.engine.ImageGenerator
import com.example.frametext.engine.TextFormattingDetails
import com.example.frametext.globalObjects.FrameTextParameters
import com.example.frametext.shapes.edge.EdgeShapeDetails
import com.example.frametext.userControls.AlertPopupOK
import com.example.frametext.viewModels.FrameTextBitmapViewModel
import com.example.frametext.viewModels.FrameTextParametersViewModel
import com.example.frametext.viewModels.TabLayoutViewModel
import com.example.frametext.viewModels.TextInputViewModel
import com.google.android.material.tabs.TabLayout

class TextInputFragment : Fragment() {
    private var fragmentActivityContext: FragmentActivity? = null
    private var textInputViewModel: TextInputViewModel? = null
    private var frameTextBitmapViewModel: FrameTextBitmapViewModel? = null
    private var tabLayoutViewModel: TabLayoutViewModel? = null
    private var frameTextParametersViewModel: FrameTextParametersViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        container?.removeAllViews()
        val view =  inflater.inflate(R.layout.fragment_text_input, container, false)

        val generateImageButton = view.findViewById<View>(R.id.generateImageButton)
        generateImageButton.setOnClickListener { generateImage() }

        val loadSaveFileNavButton = view.findViewById<View>(R.id.loadSaveFileNavButton)
        loadSaveFileNavButton.setOnClickListener { onExitToUserFilesFragment(view) }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentActivityContext = context as FragmentActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textInputViewModel = ViewModelProvider(requireActivity())[TextInputViewModel::class.java]
        frameTextBitmapViewModel = ViewModelProvider(requireActivity())[FrameTextBitmapViewModel::class.java]
        tabLayoutViewModel = ViewModelProvider(requireActivity())[TabLayoutViewModel::class.java]
        frameTextParametersViewModel = ViewModelProvider(requireActivity())[FrameTextParametersViewModel::class.java]

        val editTextInput = view.findViewById<EditText>(R.id.editTextInput)
        editTextInput.setText(textInputViewModel!!.getSelectedItem().value)
    }

    private fun onExitToUserFilesFragment(view: View) {
        val editTextInput = view.findViewById<EditText>(R.id.editTextInput)
        textInputViewModel?.selectItem(editTextInput.text.toString())
        setToUserFilesFragment()
    }

    private fun setToUserFilesFragment() {
        fragmentActivityContext?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.text_input_frame, UserFilesFragment())
            ?.setReorderingAllowed(true)
            ?.commit()
    }

    private fun generateImage() {
        try {
            val ftp: FrameTextParameters? = frameTextParametersViewModel?.getSelectedItem()?.value
            if (ftp != null && context != null) {
                val editTextInput = requireView().findViewById<EditText>(R.id.editTextInput)
                val strTextInput = editTextInput.text.toString()
                val tfd = TextFormattingDetails(
                    strTextInput, ftp.optimizeSpacing, ftp.hyphenateText,
                    ftp.hyphenFileName, 50, 170, ftp.getTxtSymbolsMargin(), ftp.textColor, ftp.fontFamily, ftp.typeface
                )
                val edgeShapeDetails: EdgeShapeDetails? = ftp.getShapeDetails()
                if (edgeShapeDetails != null) {
                    val frameTextImgContainer = ImageGenerator(
                        tfd,
                        ftp.mainShapeType,
                        edgeShapeDetails,
                        ftp.backgroundColor,
                        ftp.outerMargin,
                        ftp.minDistEdgeShape,
                        requireContext()
                    )

                    frameTextImgContainer.computeTextFit(requireContext())
                    frameTextImgContainer.draw()
                    val frameTextImage: Bitmap = frameTextImgContainer.bitmap

                    frameTextBitmapViewModel?.selectItem(frameTextImage)

                    frameTextBitmapViewModel?.getSelectedImageFragment()?.value
                        ?.updateImage(frameTextImgContainer.bitmap)

                    val tabLayout: TabLayout? =
                        tabLayoutViewModel?.getSelectedItem()?.value
                    if (tabLayout != null) {
                        if (tabLayout.tabCount == 2)
                            tabLayout.newTab().setText(resources.getString(R.string.image))
                                .let { tabLayout.addTab(it) }

                        val tab = tabLayout.getTabAt(2)
                        tab?.select()
                    }
                }
            }
        } catch (e: FrameTextException) {
            AlertPopupOK(generateImageTitle(), e.message ?: "").show(requireView(), requireContext())
        } catch (e: Exception) {
            AlertPopupOK(generateImageTitle(), getUnexpectedErrorMessage() + "\n" + e.message).show(requireView(), requireContext())
        }
    }

    private fun generateImageTitle(): String {
        var generateImageTitle = "Generate image"
        if (context != null && requireContext().resources != null) {
            generateImageTitle = requireContext().resources.getString(R.string.generateImage)
        }
        return generateImageTitle
    }

    private fun getUnexpectedErrorMessage(): String {
        val unexpectedError = "An unexpected error occurred."
        if (context != null && requireContext().resources != null) {
            requireContext().resources.getString(R.string.error_unexpected)
        }
        return unexpectedError
    }
}