package com.example.frametext.fragments

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.frametext.MainActivity
import com.example.frametext.R
import com.example.frametext.globalObjects.FrameTextParameters
import com.example.frametext.helpers.MinMaxFilter
import com.example.frametext.userControls.AlertPopupOKCancel
import com.example.frametext.userControls.colorPicker.ColorPickerPopup
import com.example.frametext.viewModels.FileNameHplMapViewModel
import com.example.frametext.viewModels.FrameTextParametersViewModel
import com.example.frametext.viewModels.HplFileNameMapViewModel
import com.example.frametext.viewModels.HyphenFilesListViewModel
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*


class SettingsFragment : Fragment() {
    private var spinner: Spinner? = null
    private var needToDownloadText: TextView? = null
    private var hyphenateSwitch: SwitchCompat? = null
    var selectedItem: String? = null
    private var hyphenFilesList: ArrayList<String>? = null
    var hplFileNameMap: HashMap<String, String>? = null
    private var fileNameHplMap: HashMap<String, String>? = null
    var ftp: FrameTextParameters? = null
    private var neededToDownloadText = false
    private var wasHyphenFileListEmpty = false
    private var fragmentActivityContext: FragmentActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        container?.removeAllViews()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentActivityContext = context as FragmentActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        val frameTextParametersViewModel: FrameTextParametersViewModel =
            ViewModelProvider(requireActivity())[FrameTextParametersViewModel::class.java]
        ftp = frameTextParametersViewModel.getSelectedItem().value
        val hyphenFilesListViewModel: HyphenFilesListViewModel =
            ViewModelProvider(requireActivity())[HyphenFilesListViewModel::class.java]
        hyphenFilesList = hyphenFilesListViewModel.selectedItem.value
        val hplFileNameMapViewModel: HplFileNameMapViewModel =
            ViewModelProvider(requireActivity())[HplFileNameMapViewModel::class.java]
        hplFileNameMap = hplFileNameMapViewModel.getSelectedItem().value
        val fileNameHplMapViewModel: FileNameHplMapViewModel =
            ViewModelProvider(requireActivity())[FileNameHplMapViewModel::class.java]
        fileNameHplMap = fileNameHplMapViewModel.getSelectedItem().value

        val button = view.findViewById<View>(R.id.downloadHyphenNavButton)
        button.setOnClickListener { navigateToHyphenationFragment() }

        spinner = view.findViewById(R.id.spinner)
        needToDownloadText = view.findViewById(R.id.needToDownLoad)
        hyphenateSwitch = view.findViewById(R.id.hyphenateSwitch)

        fileNameHplMap?.let{
            selectedItem = it[ftp?.hyphenFileName]
        }

        updateHyphenDropdown()

        spinner?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    pos: Int,
                    id: Long
                ) {
                    val item = parent.getItemAtPosition(pos)
                    hplFileNameMap?.let {
                        if (it.containsKey(item.toString())) {
                            val fileName = it[item.toString()]
                            ftp?.hyphenFileName = fileName
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        ftp?.let { ftpIt ->
            hyphenateSwitch?.isChecked = ftpIt.hyphenateText
            hyphenateSwitch?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                ftp?.hyphenateText = isChecked
            }

            val optimizeSpacingSwitch = view.findViewById<SwitchCompat>(R.id.optimizeSpacingSwitch)
            optimizeSpacingSwitch.isChecked = ftpIt.optimizeSpacing
            optimizeSpacingSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                ftpIt.optimizeSpacing = isChecked
            }

            val symbolsToTextNumber = view.findViewById<EditText>(R.id.symbolToTextNumber)
            symbolsToTextNumber.setText(
                java.lang.String.format(
                    Locale.getDefault(),
                    "%d",
                    ftpIt.getTxtSymbolsMargin()
                ), TextView.BufferType.EDITABLE
            )
            symbolsToTextNumber?.filters = arrayOf<InputFilter>(MinMaxFilter(0, 50, { t ->
                (ftpIt.setTxtSymbolsMargin(t))
            }, { ftpIt.setTxtSymbolsMargin(0) }))

            val outerMarginNumber = view.findViewById<EditText>(R.id.outerMarginNumber)
            outerMarginNumber.setText(
                java.lang.String.format(
                    Locale.getDefault(),
                    "%d",
                    ftpIt.outerMargin
                ), TextView.BufferType.EDITABLE
            )
            outerMarginNumber?.filters = arrayOf<InputFilter>(MinMaxFilter(0, 500, { t ->
                ftpIt.outerMargin = t }, { ftpIt.outerMargin = 0 }))

            val buttonNavToFrmShapeSettings = view.findViewById<View>(R.id.frameShapeSettings)
            buttonNavToFrmShapeSettings.setOnClickListener { navigateToFrameShapeSettingsFragment() }

            val backgroundColorButton = view.findViewById<AppCompatButton>(R.id.backgroundColorButton)
            backgroundColorButton.setBackgroundColor(ftpIt.backgroundColor)
            backgroundColorButton.setOnClickListener { v: View? ->
                onClickBackgroundColorButton(
                    v,
                    backgroundColorButton
                )
            }
        }

        val buttonNavToFontSettings = view.findViewById<View>(R.id.font_settings)
        buttonNavToFontSettings.setOnClickListener { navigateToFontSettingsFragment() }

        val buttonNavToNewFeatureSetting = view.findViewById<View>(R.id.newFeatures)
        buttonNavToNewFeatureSetting.setOnClickListener { navigateToNewFeaturesFragment() }

        val saveSettingsButton = view.findViewById<View>(R.id.saveSettings)
        saveSettingsButton.setOnClickListener { saveSettings() }

        val deleteSettingsButton = view.findViewById<View>(R.id.deleteSettings)
        deleteSettingsButton.setOnClickListener { deleteSettings() }

    }

    fun saveSelectedItem() {
        spinner?.let { spinnerIt->
            selectedItem = spinnerIt.selectedItem as String
        }
        hyphenFilesList?.let{ hyphenFilesListIt ->
            neededToDownloadText = hyphenFilesListIt.isEmpty()
        }
    }

    private fun navigateToHyphenationFragment() {
        val fragment: Fragment = HyphenFilesFragment()
        fragmentActivityContext?.let {fragmentActivityContextIt ->
            val fragmentManager = fragmentActivityContextIt.supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.settings_frame, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun navigateToFrameShapeSettingsFragment() {
        val fragment: Fragment = FrameShapesFragment()
        fragmentActivityContext?.let { fragmentActivityContextIt ->
            val fragmentManager = fragmentActivityContextIt.supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.settings_frame, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun navigateToFontSettingsFragment() {
        val fragment: Fragment = FontSettingsFragment()
        fragmentActivityContext?.let { fragmentActivityContextIt ->
            val fragmentManager = fragmentActivityContextIt.supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.settings_frame, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun navigateToNewFeaturesFragment() {
        val fragment: Fragment = NewFeaturesFragment()
        fragmentActivityContext?.let { fragmentActivityContextIt ->
            val fragmentManager = fragmentActivityContextIt.supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.settings_frame, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun onClickBackgroundColorButton(v: View?, backgroundColorButton: AppCompatButton) {
        context?.let {
            ftp?.let { it1 ->
                ColorPickerPopup.Builder(it).initialColor(it1.backgroundColor)
                    .enableBrightness(true)
                    .enableAlpha(true)
                    .okTitle(resources.getString(R.string.select))
                    .cancelTitle(resources.getString(R.string.cancel))
                    .showIndicator(true)
                    .showValue(true)
                    .build()
                    .show(v,
                        object : ColorPickerPopup.ColorPickerObserver() {
                            override fun onColorPicked(color: Int) {
                                backgroundColorButton.setBackgroundColor(color)
                                it1.backgroundColor = color
                            }
                        })
            }
        }
    }

    fun updateHyphenDropdown() {
        if (hyphenFilesList?.isEmpty() == true) {
            needToDownloadText?.visibility = View.VISIBLE
            spinner?.visibility = View.GONE
            hyphenateSwitch?.visibility = View.GONE
            wasHyphenFileListEmpty = true
        } else {
            needToDownloadText?.visibility = View.GONE
            spinner?.visibility = View.VISIBLE
            hyphenateSwitch?.visibility = View.VISIBLE
            val context = this.context

            // If the hyphenFile list was empty and has just been filled, we switch hyphenation on.
            if (wasHyphenFileListEmpty) {
                hyphenateSwitch?.isChecked = true
            }
            wasHyphenFileListEmpty = false
            context?.let {
                hyphenFilesList?.let { hyphenFilesListIt ->
                    val arrayAdapter = ArrayAdapter(
                        it,
                        R.layout.spinner_list, hyphenFilesListIt
                    )
                    arrayAdapter.setDropDownViewResource(R.layout.spinner_list)

                    spinner?.let { spinnerIt ->
                        spinnerIt.adapter = arrayAdapter

                        selectedItem?.let { selectedItemIt ->
                            if (hyphenFilesListIt.contains(selectedItemIt)) {
                                val pos = hyphenFilesListIt.indexOf(selectedItemIt)
                                spinnerIt.setSelection(pos)
                            }
                        }
                    }
                }
            }
            if (neededToDownloadText) {
                hyphenateSwitch?.isChecked = true
            }
        }
    }

    private fun saveSettings() {
        try {
            val jsonObj: JSONObject? = ftp?.let {
                MainActivity.getJSonObjectFromFramesTextParameters(
                    it
                )
            }
            val hvpSettingsString = jsonObj.toString()
            context ?: throw Exception("getContext returned null.")

            val okFuncPtr : () -> Unit = {
                context?.let { MainActivity.getSettingsFileName(it) }?.let{ settingsPathNameIt ->
                    val file = File(settingsPathNameIt)
                    val fileWriter = FileWriter(file)
                    val bufferedWriter = BufferedWriter(fileWriter)
                    bufferedWriter.write(hvpSettingsString)
                    bufferedWriter.close()
                }
            }

            val alertDialog = AlertPopupOKCancel(requireContext().resources.getString(R.string.saveSettings),
                requireContext().resources.getString(R.string.saveSettingsInfo),
                okFuncPtr)

            alertDialog.show(requireView(), requireContext())

        } catch (e: Exception) {
            println("An error occurred in method saveSettings().")
            e.printStackTrace()
        }
    }

    private fun deleteSettings() {
        try {
            val context = context ?: throw Exception("getContext returned null.")
            MainActivity.getSettingsFileName(context)?.let {
                val file = File(it)
                if (file.exists()) {
                    val okFuncPtr = {
                        file.delete()
                        Unit
                    }

                    val alertDialog = AlertPopupOKCancel(requireContext().resources.getString(R.string.deleteSettings),
                        requireContext().resources.getString(R.string.deleteSettingsInfo),
                        okFuncPtr)

                    alertDialog.show(requireView(), requireContext())
                }
            }
        } catch (e: Exception) {
            println("An error occurred in method saveSettings().")
            e.printStackTrace()
        }
    }
}