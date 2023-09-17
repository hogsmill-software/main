package com.example.frametext.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.frametext.R
import com.example.frametext.userControls.AlertPopupOK
import com.example.frametext.viewModels.FrameTextBitmapViewModel

class FrameTextImageFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frame_text_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val frameTextBitmapViewModel: FrameTextBitmapViewModel =
            ViewModelProvider(requireActivity()).get(
                FrameTextBitmapViewModel::class.java
            )
        val bm: Bitmap? = frameTextBitmapViewModel.getSelectedItem().value
        frameTextBitmapViewModel.selectImageFragment(this)

        if (bm != null) {
            val imageView = view.findViewById<ImageView>(R.id.imageView)
            imageView.setImageBitmap(bm)
        }

        val saveImageButton = view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.saveImageButton)
        saveImageButton.setOnClickListener { saveImage() }
    }

    fun updateImage(bm: Bitmap?) {
        if (bm != null && view != null) {
            val imageView = requireView().findViewById<ImageView>(R.id.imageView)
            imageView.setImageBitmap(bm)
        }
    }

    private fun saveImage() {
        try {
            val imageView = requireView().findViewById<ImageView>(R.id.imageView)
            val bitMap = imageView.drawable.toBitmap()

            // generate image file in Pictures folder:
            if (activity != null) {
                val fileName = "FT-" + System.currentTimeMillis()
                MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                bitMap,
                fileName,
                "Generated through FrameText."
                )
                AlertPopupOK(requireContext().resources.getString(R.string.save_image_title), requireContext().resources.getString(R.string.save_image_content)).show(requireView(), requireContext())
            }
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