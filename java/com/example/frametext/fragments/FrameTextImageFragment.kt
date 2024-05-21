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
            ViewModelProvider(requireActivity())[FrameTextBitmapViewModel::class.java]
        val bm: Bitmap? = frameTextBitmapViewModel.getSelectedItem().value
        frameTextBitmapViewModel.selectImageFragment(this)

        bm?.let {
            val imageView = view.findViewById<ImageView>(R.id.imageView)
            imageView.setImageBitmap(it)
        }

        val saveImageButton = view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.saveImageButton)
        saveImageButton.setOnClickListener { saveImage() }
    }

    fun updateImage(bm: Bitmap?) {
        bm?.let {
            val imageView = requireView().findViewById<ImageView>(R.id.imageView)
            imageView.setImageBitmap(it)
        }
    }

    private fun saveImage() {
        try {
            val imageView = requireView().findViewById<ImageView>(R.id.imageView)
            val bitMap = imageView.drawable.toBitmap()

            // generate image file in Pictures folder:
            activity?.let {
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
        context?.let{
            requireContext().resources?.let {
                generateImageTitle = it.getString(R.string.generateImage)
            }
        }
        return generateImageTitle
    }

    private fun getUnexpectedErrorMessage(): String {
        var unexpectedError = "An unexpected error occurred."
        context?.let{
            requireContext().resources?.let {
                unexpectedError = it.getString(R.string.error_unexpected)
            }
        }
        return unexpectedError
    }
}