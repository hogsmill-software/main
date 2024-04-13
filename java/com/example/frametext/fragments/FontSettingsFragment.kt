package com.example.frametext.fragments

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.frametext.R
import com.example.frametext.globalObjects.FrameTextParameters
import com.example.frametext.helpers.Utilities
import com.example.frametext.userControls.colorPicker.ColorPickerPopup
import com.example.frametext.viewModels.FontFamilyUserFriendlyFontFamilyMapViewModel
import com.example.frametext.viewModels.FrameTextParametersViewModel
import com.example.frametext.viewModels.TypesetIdUserFriendlyFontFamilyMapViewModel
import com.example.frametext.viewModels.UserFriendlyFontFamilyFontFamilyMapViewModel
import com.example.frametext.viewModels.UserFriendlyFontFamilyTypesetIdMapViewModel
import java.util.ArrayList

class FontSettingsFragment : Fragment() {
    private var fragmentActivityContext: FragmentActivity? = null
    private var ftp: FrameTextParameters? = null
    private var spinnerFontFamilies: Spinner? = null
    private var userFriendlyFontFamilyList: ArrayList<String> = Utilities.userFriendlyFontFamilyList()
    private var boldSwitch: androidx.appcompat.widget.SwitchCompat? = null
    private var italicSwitch: androidx.appcompat.widget.SwitchCompat? = null
    private var typefaceIdSet: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentActivityContext = context as FragmentActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        container?.removeAllViews()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_font_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val heartValParametersViewModel = ViewModelProvider(requireActivity())[FrameTextParametersViewModel::class.java]
        val button = view.findViewById<View>(R.id.backButton)
        button.setOnClickListener { navigateToSettingsFragment() }

        ftp = heartValParametersViewModel.getSelectedItem().value

        val textColorButton = view.findViewById<AppCompatButton>(R.id.textColorButton)
        textColorButton.setBackgroundColor(ftp!!.textColor)
        textColorButton.setOnClickListener { v: View? ->
            onClickTextColorButton(
                v,
                textColorButton
            )
        }

        spinnerFontFamilies = view.findViewById(R.id.spinnerFontFamilies)

        if (context != null) {
            val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_list, userFriendlyFontFamilyList
            )
            arrayAdapter.setDropDownViewResource(R.layout.spinner_list)
            spinnerFontFamilies!!.adapter = arrayAdapter
        }

        val userFriendlySelectedItem : String? = if (ftp!!.fontFamily != "") {
            val fontFamilyUserFriendlyFontFamilyMapViewModel =
                ViewModelProvider(requireActivity())[FontFamilyUserFriendlyFontFamilyMapViewModel::class.java]

            val fontFamilyUserFriendlyFontFamilyMap =
                fontFamilyUserFriendlyFontFamilyMapViewModel.getSelectedItem().value
            fontFamilyUserFriendlyFontFamilyMap?.get(ftp!!.fontFamily)
        } else {
            val typesetIdUserFriendlyFontFamilyMapViewModel = ViewModelProvider(requireActivity())[TypesetIdUserFriendlyFontFamilyMapViewModel::class.java]

            val typesetIdUserFriendlyFontFamilyMap = typesetIdUserFriendlyFontFamilyMapViewModel.getSelectedItem().value
            typesetIdUserFriendlyFontFamilyMap?.get(ftp!!.typefaceId)
        }

        val pos = userFriendlyFontFamilyList.indexOf(userFriendlySelectedItem)

        spinnerFontFamilies!!.setSelection(pos)

        val userFriendlyFontFamilyFontFamilyMapViewModel = ViewModelProvider(requireActivity())[UserFriendlyFontFamilyFontFamilyMapViewModel::class.java]
        val userFriendlyFontFamilyFontFamilyMap = userFriendlyFontFamilyFontFamilyMapViewModel.getSelectedItem().value

        val userFriendlyFontFamilyTypesetIdMapViewModel = ViewModelProvider(requireActivity())[UserFriendlyFontFamilyTypesetIdMapViewModel::class.java]
        val userFriendlyFontFamilyTypesetIdMap = userFriendlyFontFamilyTypesetIdMapViewModel.getSelectedItem().value

        boldSwitch = view.findViewById(R.id.boldSwitch)
        italicSwitch = view.findViewById(R.id.italicSwitch)

        spinnerFontFamilies?.onItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
            parent: AdapterView<*>,
            view: View,
            pos: Int,
            id: Long
            ) {
                val userFriendlyFontFamily = parent.getItemAtPosition(pos)
                val fontFamily = userFriendlyFontFamilyFontFamilyMap?.get(userFriendlyFontFamily)

                if (fontFamily != null) {
                    ftp?.fontFamily = fontFamily
                    typefaceIdSet = false
                    boldSwitch?.isEnabled = true
                    italicSwitch?.isEnabled = true
                    boldSwitch?.isChecked = ftp!!.fontStyle == Typeface.BOLD || ftp!!.fontStyle == Typeface.BOLD_ITALIC
                    italicSwitch?.isChecked = ftp!!.fontStyle == Typeface.ITALIC || ftp!!.fontStyle == Typeface.BOLD_ITALIC
                } else {
                    ftp?.fontFamily = ""
                    val typeFaceId = userFriendlyFontFamilyTypesetIdMap?.get(userFriendlyFontFamily)

                    if (typeFaceId != null) {
                        ftp?.typefaceId =  typeFaceId
                        typefaceIdSet = true
                        boldSwitch?.isChecked = false
                        italicSwitch?.isChecked = false
                        boldSwitch?.isEnabled = false
                        italicSwitch?.isEnabled = false
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        if (!typefaceIdSet) {
            boldSwitch?.isChecked = ftp!!.fontStyle == Typeface.BOLD || ftp!!.fontStyle == Typeface.BOLD_ITALIC

            boldSwitch?.setOnCheckedChangeListener { _: CompoundButton?, isBoldChecked: Boolean ->
                if (isBoldChecked) {
                    if (ftp!!.fontStyle == Typeface.NORMAL) {
                        ftp!!.fontStyle = Typeface.BOLD
                    } else if (ftp!!.fontStyle == Typeface.ITALIC) {
                        ftp!!.fontStyle = Typeface.BOLD_ITALIC
                    }
                } else {
                    if (ftp!!.fontStyle == Typeface.BOLD) {
                        ftp!!.fontStyle = Typeface.NORMAL
                    } else if (ftp!!.fontStyle == Typeface.BOLD_ITALIC) {
                        ftp!!.fontStyle = Typeface.ITALIC
                    }
                }
            }

            italicSwitch?.isChecked = ftp!!.fontStyle == Typeface.ITALIC || ftp!!.fontStyle == Typeface.BOLD_ITALIC

            italicSwitch?.setOnCheckedChangeListener { _: CompoundButton?, isItalicChecked: Boolean ->
                if (isItalicChecked) {
                    if (ftp!!.fontStyle == Typeface.NORMAL) {
                        ftp!!.fontStyle = Typeface.ITALIC
                    } else if (ftp!!.fontStyle == Typeface.BOLD) {
                        ftp!!.fontStyle = Typeface.BOLD_ITALIC
                    }
                } else {
                    if (ftp!!.fontStyle == Typeface.ITALIC) {
                        ftp!!.fontStyle = Typeface.NORMAL
                    } else if (ftp!!.fontStyle == Typeface.BOLD_ITALIC) {
                        ftp!!.fontStyle = Typeface.BOLD
                    }
                }
            }
        }
    }

    private fun navigateToSettingsFragment() {
        val fragment: Fragment = SettingsFragment()
        val fragmentManager = fragmentActivityContext!!.supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.settings_frame, fragment)
            .setReorderingAllowed(true)
            .commit()
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
}