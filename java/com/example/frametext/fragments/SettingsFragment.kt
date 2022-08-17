package com.example.frametext.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            ViewModelProvider(requireActivity()).get(
                FrameTextParametersViewModel::class.java
            )
        ftp = frameTextParametersViewModel.getSelectedItem().value
        val hyphenFilesListViewModel: HyphenFilesListViewModel =
            ViewModelProvider(requireActivity()).get(
                HyphenFilesListViewModel::class.java
            )
        hyphenFilesList = hyphenFilesListViewModel.selectedItem.value
        val hplFileNameMapViewModel: HplFileNameMapViewModel =
            ViewModelProvider(requireActivity()).get(
                HplFileNameMapViewModel::class.java
            )
        hplFileNameMap = hplFileNameMapViewModel.getSelectedItem().value
        val fileNameHplMapViewModel: FileNameHplMapViewModel =
            ViewModelProvider(requireActivity()).get(
                FileNameHplMapViewModel::class.java
            )
        fileNameHplMap = fileNameHplMapViewModel.getSelectedItem().value

        val button = view.findViewById<View>(R.id.downloadHyphenNavButton)
        button.setOnClickListener { navigateToHyphenationFragment() }

        spinner = view.findViewById(R.id.spinner)
        needToDownloadText = view.findViewById(R.id.needToDownLoad)
        hyphenateSwitch = view.findViewById(R.id.hyphenateSwitch)

        selectedItem = fileNameHplMap!![ftp?.hyphenFileName]

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
                    if (hplFileNameMap!!.containsKey(item.toString())) {
                        val fileName = hplFileNameMap!![item.toString()]
                        ftp?.hyphenFileName = fileName
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        hyphenateSwitch?.isChecked = ftp!!.hyphenateText
        hyphenateSwitch?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            ftp?.hyphenateText = isChecked
        }

        val optimizeSpacingSwitch = view.findViewById<SwitchCompat>(R.id.optimizeSpacingSwitch)
        optimizeSpacingSwitch.isChecked = ftp!!.optimizeSpacing
        optimizeSpacingSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            ftp!!.optimizeSpacing = isChecked
        }

        val symbolsToTextNumber = view.findViewById<EditText>(R.id.symbolToTextNumber)
        symbolsToTextNumber.setText(
            java.lang.String.format(
                Locale.getDefault(),
                "%d",
                ftp!!.getTxtSymbolsMargin()
            ), TextView.BufferType.EDITABLE
        )
        symbolsToTextNumber.filters = arrayOf(MinMaxFilter("0", "50"))
        symbolsToTextNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    ftp!!.setTxtSymbolsMargin(s.toString().toInt())
                } else {
                    ftp!!.setTxtSymbolsMargin(0)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        val outerMarginNumber = view.findViewById<EditText>(R.id.outerMarginNumber)
        outerMarginNumber.setText(
            java.lang.String.format(
                Locale.getDefault(),
                "%d",
                ftp!!.outerMargin
            ), TextView.BufferType.EDITABLE
        )
        outerMarginNumber.filters = arrayOf<InputFilter>(MinMaxFilter("0", "500"))
        outerMarginNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                   ftp!!.outerMargin = s.toString().toInt()
                } else {
                    ftp!!.outerMargin = 0
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        val buttonNavToFrmShapeSettings = view.findViewById<View>(R.id.frameShapeSettings)
        buttonNavToFrmShapeSettings.setOnClickListener { navigateToFrameShapeSettingsFragment() }

        val backgroundColorButton = view.findViewById<AppCompatButton>(R.id.backgroundColorButton)
        backgroundColorButton.setBackgroundColor(ftp!!.backgroundColor)
        backgroundColorButton.setOnClickListener { v: View? ->
            onClickBackgroundColorButton(
                v,
                backgroundColorButton
            )
        }

        val buttonNavToFontSettings = view.findViewById<View>(R.id.font_settings)
        buttonNavToFontSettings.setOnClickListener { navigateToFontSettingsFragment() }

        val saveSettingsButton = view.findViewById<View>(R.id.saveSettings)
        saveSettingsButton.setOnClickListener { saveSettings() }

        val deleteSettingsButton = view.findViewById<View>(R.id.deleteSettings)
        deleteSettingsButton.setOnClickListener { deleteSettings() }

    }

    fun saveSelectedItem() {
   //     selectedItem = spinner.getSelectedItem() as String
   //     neededToDownloadText = hyphenFilesList.isEmpty()
    }

    private fun navigateToHyphenationFragment() {
        val fragment: Fragment = HyphenFilesFragment()
        val fragmentManager = fragmentActivityContext!!.supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.settings_frame, fragment)
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToFrameShapeSettingsFragment() {
        val fragment: Fragment = FrameShapesFragment()
        val fragmentManager = fragmentActivityContext!!.supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.settings_frame, fragment)
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToFontSettingsFragment() {
        val fragment: Fragment = FontSettingsFragment()
        val fragmentManager = fragmentActivityContext!!.supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.settings_frame, fragment)
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
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
                                ftp!!.backgroundColor = color
                            }
                        })
            }
        }
    }

    private fun onClickTextColorButton(v: View?, textColorButton: AppCompatButton) {
        context?.let {
            ftp?.let { it1 ->
                ColorPickerPopup.Builder(it).initialColor(it1.textColor)
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
                                textColorButton.setBackgroundColor(color)
                                ftp!!.textColor = color
                            }
                        })
            }
        }
    }


    fun updateHyphenDropdown() {
        if (hyphenFilesList!!.isEmpty()) {
            needToDownloadText!!.visibility = View.VISIBLE
            spinner!!.visibility = View.GONE
            hyphenateSwitch!!.visibility = View.GONE
            wasHyphenFileListEmpty = true
        } else {
            needToDownloadText!!.visibility = View.GONE
            spinner!!.visibility = View.VISIBLE
            hyphenateSwitch!!.visibility = View.VISIBLE
            val context = this.context

            // If the hyphenFile list was empty and has just been filled, we switch hyphenation on.
            if (wasHyphenFileListEmpty) {
                hyphenateSwitch!!.isChecked = true
            }
            wasHyphenFileListEmpty = false
            if (context != null) {
                val arrayAdapter = ArrayAdapter(
                    context,
                    R.layout.spinner_list, hyphenFilesList!!
                )
                arrayAdapter.setDropDownViewResource(R.layout.spinner_list)
                spinner!!.adapter = arrayAdapter
            }
            if (selectedItem != null && hyphenFilesList!!.contains(selectedItem!!)) {
                val pos = hyphenFilesList!!.indexOf(selectedItem!!)
                spinner!!.setSelection(pos)
            }
            if (neededToDownloadText) {
                hyphenateSwitch!!.isChecked = true
            }
        }
    }

    /*
    void activateDeactivateHeartColorButton(View view, boolean activate) {
        // This methods just gives the disable look.
        TextView heartColorText = view.findViewById(R.id.heartColorText);
        heartColorText.setTextColor(activate? Color.BLACK : Color.GRAY);

        View heartColorButtonFrame  = view.findViewById(R.id.heartColorButtonFrame);
        if (getContext() != null && getContext().getResources() != null) {
            heartColorButtonFrame.setBackgroundColor(activate ? getContext().getResources().getColor(R.color.highlightBlue) : getContext().getResources().getColor(R.color.midDayFog));
        }
        androidx.appcompat.widget.AppCompatButton heartsColorButton = view.findViewById(R.id.heartsColorButton);
        heartsColorButton.setBackgroundColor(activate? hvp.getHeartsColor() : hvp.getHeartsColor() & 0x88FFFFFF);
    }
    */
    private fun saveSettings() {
        try {
            val jsonObj: JSONObject? = ftp?.let {
                MainActivity.getJSonObjectFromFramesTextParameters(
                    it
                )
            }
            val hvpSettingsString = jsonObj.toString()
            context ?: throw Exception("getContext returned null.")
            val settingsPathName: String? = context?.let { MainActivity.getSettingsFileName(it) }
            if (settingsPathName != null) {
                val file = File(settingsPathName)
                val fileWriter = FileWriter(file)
                val bufferedWriter = BufferedWriter(fileWriter)
                bufferedWriter.write(hvpSettingsString)
                bufferedWriter.close()
                if (context != null) {
                    AlertDialog.Builder(context)
                        .setTitle(requireContext().resources.getString(R.string.saveSettings))
                        .setMessage(requireContext().resources.getString(R.string.saveSettingsInfo))
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.alert_light_frame)
                        .show()
                }
            }
        } catch (e: Exception) {
            println("An error occurred in method saveSettings().")
            e.printStackTrace()
        }
    }

    private fun deleteSettings() {
        try {
            val context = context ?: throw Exception("getContext returned null.")
            val settingsPathName: String? = MainActivity.getSettingsFileName(context)
            if (settingsPathName != null) {
                val file = File(settingsPathName)
                if (file.exists()) {
                    if (file.delete() && getContext() != null) {
                        AlertDialog.Builder(getContext())
                            .setTitle(requireContext().resources.getString(R.string.deleteSettings))
                            .setMessage(requireContext().resources.getString(R.string.deleteSettingsInfo))
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.alert_light_frame)
                            .show()
                    }
                }
            }
        } catch (e: Exception) {
            println("An error occurred in method saveSettings().")
            e.printStackTrace()
        }
    }
}