package com.example.frametext.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frametext.MainActivity
import com.example.frametext.R
import com.example.frametext.adapters.UserFileListAdapter
import com.example.frametext.engine.FrameTextException
import com.example.frametext.engine.ImageGenerator
import com.example.frametext.engine.TextFormattingDetails
import com.example.frametext.globalObjects.FrameTextParameters
import com.example.frametext.userControls.AlertPopupOK
import com.example.frametext.viewModels.FrameTextParametersViewModel
import com.example.frametext.viewModels.TextInputViewModel
import com.example.frametext.viewModels.UserFilesViewModel
import java.io.File
import java.io.FileWriter
import java.io.IOException

class UserFilesFragment : Fragment() {

    private var ftp: FrameTextParameters? = null
    private var userFileRecyclerView: androidx.recyclerview.widget.RecyclerView? = null
    private var fragmentActivityContext: FragmentActivity? = null
    private var userFileList: ArrayList<String>? = null
    private var textInputViewModel: TextInputViewModel? = null
    private var userFileListAdapter: UserFileListAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        container?.removeAllViews()
        val view =  inflater.inflate(R.layout.fragment_user_files, container, false)

        val backButton = view.findViewById<View>(R.id.backButton)
        backButton.setOnClickListener { setToTextInputFragment() }

        val saveUserFileButton = view.findViewById<View>(R.id.saveFileButton)
        saveUserFileButton.setOnClickListener { saveFile(view) }

        userFileRecyclerView = view.findViewById(R.id.userFileRecyclerView)
        linearLayoutManager = LinearLayoutManager(context)
        userFileRecyclerView?.layoutManager = linearLayoutManager

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentActivityContext = context as FragmentActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textInputViewModel = ViewModelProvider(requireActivity())[TextInputViewModel::class.java]
        val userFilesViewModel: UserFilesViewModel = ViewModelProvider(requireActivity())[UserFilesViewModel::class.java]
        userFileList = userFilesViewModel.selectedItem.value
        val heartValParametersViewModel: FrameTextParametersViewModel =
            ViewModelProvider(requireActivity())[FrameTextParametersViewModel::class.java]
        ftp = heartValParametersViewModel.getSelectedItem().value

        textInputViewModel?.let {
            userFileListAdapter = UserFileListAdapter(view.context, userFileList, it, this)

            userFileRecyclerView?.let { userFileRecyclerViewIt ->
                userFileRecyclerViewIt.adapter = userFileListAdapter
            }
        }

        // Add dividers
        val dividerItemDecoration = DividerItemDecoration(
            userFileRecyclerView?.context,
            linearLayoutManager.orientation
        )
        userFileRecyclerView?.addItemDecoration(dividerItemDecoration)
    }


    fun setToTextInputFragment() {
        fragmentActivityContext?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.text_input_frame, TextInputFragment())
            ?.setReorderingAllowed(true)
            ?.commit()
    }

    private fun saveFile(view: View) {
        try {
            val fileName = view.findViewById<EditText>(R.id.fileName)
            val strFileName = fileName.text.toString()
            if (strFileName.isEmpty()) {
                throw FrameTextException(enterFileNameErrMsg())
            }
            val strTextInput = textInputViewModel?.getSelectedItem()?.value
                ?: throw Exception("strTextInput should never be null.")
            if (ftp == null) {
                throw Exception("ftp should never be null.")
            }
            ftp?.let{
                val tfd = TextFormattingDetails(
                    strTextInput, it.optimizeSpacing, it.hyphenateText,
                    it.hyphenFileName, 50, 170, it.getTxtSymbolsMargin(), it.textColor,  it.fontFamily, it.typefaceId, it.fontStyle
                )
                it.getShapeDetails()?.let { getShapeDetailsIt ->
                    val imageGenerator = ImageGenerator(
                        tfd,
                        it.mainShapeType,
                        getShapeDetailsIt,
                        it.backgroundColor,
                        it.outerMargin,
                        it.minDistEdgeShape,
                        requireContext()
                    )
                    imageGenerator.computeTextFit(requireContext())
                }
            }

            val userFile =
                File(MainActivity.getUserFileFolder(true, requireContext()).toString() + strFileName)
            val writer = FileWriter(userFile)
            writer.append(strTextInput)
            writer.flush()
            writer.close()
            userFileList?.add(strFileName)
            userFileListAdapter?.let {
                it.notifyItemInserted(it.itemCount)
            }
            fileName.setText("")
        } catch (e: FrameTextException) {
            AlertPopupOK(generateImageTitle(), e.message ?: "").show(requireView(), requireContext())
        } catch (e: IOException) {
            AlertPopupOK(generateImageTitle(), e.message ?: "").show(requireView(), requireContext())
        } catch (e: Exception) {
            AlertPopupOK(generateImageTitle(), e.message ?: "").show(requireView(), requireContext())
            println("An error occurred in method UserFiles::saveFile.")
            e.printStackTrace()
        }
    }

    private fun generateImageTitle(): String {
        var generateImageTitle = "Generate image"
        context?.let {
            requireContext().resources?.let {
                generateImageTitle = requireContext().resources.getString(R.string.generateImage)
            }
        }
        return generateImageTitle
    }

    private fun enterFileNameErrMsg(): String {
        var enterFileNameErrMsg = "Enter a file name"
        context?.let {
            requireContext().resources?.let {
                enterFileNameErrMsg = requireContext().resources.getString(R.string.enter_file_name)
            }
        }
        return enterFileNameErrMsg
    }
}