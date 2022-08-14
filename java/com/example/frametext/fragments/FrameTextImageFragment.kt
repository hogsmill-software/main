package com.example.frametext.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.frametext.R
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
    }


    fun updateImage(bm: Bitmap?) {
        if (bm != null && view != null) {
            val imageView = requireView().findViewById<ImageView>(R.id.imageView)
            imageView.setImageBitmap(bm)
        }
    }

}