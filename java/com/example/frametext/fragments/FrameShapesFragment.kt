package com.example.frametext.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.view.*
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.frametext.FrameTextApplication
import com.example.frametext.R
import com.example.frametext.billing.SKU_MORE_EMOJIS
import com.example.frametext.billing.SKU_MORE_SYMBOLS
import com.example.frametext.enums.MainShapeType
import com.example.frametext.enums.SymbolShapeType
import com.example.frametext.globalObjects.FrameTextParameters
import com.example.frametext.helpers.MinMaxFilter
import com.example.frametext.helpers.Utilities
import com.example.frametext.userControls.*
import com.example.frametext.userControls.colorPicker.ColorPickerPopup
import com.example.frametext.viewModels.FrameTextParametersViewModel
import com.example.frametext.viewModels.NewFeaturesViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

class FrameShapesFragment : Fragment() {
    private lateinit var fragmentActivityContext: FragmentActivity
    lateinit var ftp: FrameTextParameters
    private lateinit var emojiButton: EmojiCellCtrl
    private lateinit var filledShapeButton: ShapeCellCtrl
    private lateinit var unfilledShapeButton: MainShapeCellCtrl
    private lateinit var emojiPopUpPt: Point
    private lateinit var shapePopUpPt: Point
    private lateinit var mainShapePopupPt: Point
    private var popUpPointsInitialized = false
    private var useEmojiSwitch = false
    private lateinit var minDistSymbols: EditText
    private lateinit var warningMsg: TextView
    private lateinit var newFeaturesViewModel: NewFeaturesViewModel
    private var purchasedMoreEmojis = false
    private var purchasedMoreSymbols = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentActivityContext = context as FragmentActivity
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val heartValParametersViewModel = ViewModelProvider(requireActivity())[FrameTextParametersViewModel::class.java]
        ftp = heartValParametersViewModel.getSelectedItem().value!!

        val symbolsColorButton = view.findViewById<AppCompatButton>(R.id.symbolsColorButton)
        symbolsColorButton.setBackgroundColor(ftp.symbolsColor)
        symbolsColorButton.setOnClickListener {
            onClickSymbolsColorButton(
                symbolsColorButton
            )
        }

        // Check in app purchase here
        val newFeaturesViewModelFactory: NewFeaturesViewModel.NewFeaturesViewModelFactory =
            NewFeaturesViewModel.NewFeaturesViewModelFactory(
                (requireActivity().application as FrameTextApplication).appContainer.storeManager
            )
        newFeaturesViewModel = ViewModelProvider(this, newFeaturesViewModelFactory)[NewFeaturesViewModel::class.java]

        val act = activity
        if (act != null) {
            newFeaturesViewModel.isPurchased(SKU_MORE_EMOJIS).observe(
                viewLifecycleOwner
            ) { purchasedMoreEmojis = it }

            newFeaturesViewModel.isPurchased(SKU_MORE_SYMBOLS).observe(
                viewLifecycleOwner
            ) { purchasedMoreSymbols = it }
        }

        val notPurchasedMoreEmojisMessage = view.findViewById<TextView>(R.id.notPurchasedMoreEmojisMessage)
        //  notPurchasedMoreEmojisMessage.visibility = if (purchasedMoreEmojis) View.GONE else View.VISIBLE
        notPurchasedMoreEmojisMessage.visibility = View.GONE

        val notPurchasedMoreSymbolsMessage = view.findViewById<TextView>(R.id.notPurchasedMoreSymbolsMessage)
        // notPurchasedMoreSymbolsMessage.visibility = if (purchasedMoreSymbols) View.GONE else View.VISIBLE
        notPurchasedMoreSymbolsMessage.visibility = View.GONE

        emojiButton = view.findViewById(R.id.emojiButton)
        emojiButton.setEmoji(ftp.emoji)
        emojiButton.setOnClickListener { openEmojiPopup() }

        filledShapeButton = view.findViewById(R.id.filledShapeButton)
        if (ftp.symbolShapeType == SymbolShapeType.None) {
            filledShapeButton.setSymbol(ftp.symbol)
        } else {
            filledShapeButton.setShapeType(ftp.symbolShapeType)
        }
        filledShapeButton.isMainButton = true
        filledShapeButton.setOnClickListener { openShapePopup() }

        activateDeactivateSymbolColorButton(view, !ftp.useEmoji)
        val useEmojiSwitch = view.findViewById<SwitchCompat>(R.id.useEmojiSwitch)

        useEmojiSwitch.isChecked = ftp.useEmoji
        useEmojiSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            ftp.useEmoji = isChecked
            activateDeactivateSymbolColorButton(view, !isChecked)
            resetMinDistEdgeShape()
        }

        unfilledShapeButton = view.findViewById(R.id.unfilledShapeButton)
        unfilledShapeButton.setShapeType(ftp.mainShapeType)
        unfilledShapeButton.isMainButton = true
        unfilledShapeButton.setOnClickListener { openMainShapePopup() }

        warningMsg = view.findViewById(R.id.warningMsg)
        minDistSymbols = view.findViewById(R.id.min_dist_symbols)

        minDistSymbols.setText(
            java.lang.String.format(
                Locale.getDefault(),
                "%d",
                ftp.minDistEdgeShape,
            ), TextView.BufferType.EDITABLE
        )

        minDistSymbols.filters = arrayOf<InputFilter>(MinMaxFilter(0, 500, ::assignMinDistEdgeShapeIfMoreThan100
        ) {   // If user has neglected to fill this value, reset this to default
            ftp.minDistEdgeShape = Utilities.closestDistance(
                ftp.useEmoji,
                ftp.emoji,
                ftp.symbol,
                ftp.symbolShapeType
            )
            warningMsg.visibility = View.VISIBLE
            warningMsg.text = String.format(requireContext().resources.let { it?.getString(R.string.blank_default_error_msg) } ?: "")
        })
    }

    private fun assignMinDistEdgeShapeIfMoreThan100(dist: Int) {
        if (dist >= 100) {
            ftp.minDistEdgeShape = dist
            warningMsg.visibility = View.GONE
        }
        else {
            warningMsg.visibility = View.VISIBLE
            warningMsg.text = String.format(requireContext().resources.getString(R.string.less_than_100_error_msg), dist)
        }
    }

    private fun initializePopUpPoints() {
        if (!popUpPointsInitialized) {
            popUpPointsInitialized = true
            emojiPopUpPt = Point()
            shapePopUpPt = Point()
            mainShapePopupPt = Point()
            val screenSizePt: Point
            val context = context
            screenSizePt = if (context != null) {
                Utilities.getRealScreenSize(context)
            } else {
                Point()
            }
            emojiPopUpPt.x = screenSizePt.x
            emojiPopUpPt.y = screenSizePt.y

            // now the shape button
            shapePopUpPt.x = screenSizePt.x
            shapePopUpPt.y = emojiPopUpPt.y

            // now the main shape popup button
            mainShapePopupPt.x = screenSizePt.x
            mainShapePopupPt.y = screenSizePt.y
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        container?.removeAllViews()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_frame_shapes, container, false)
        val button = view.findViewById<View>(R.id.backButton)
        button.setOnClickListener { navigateToSettingsFragment() }
        return view
    }

    private fun navigateToSettingsFragment() {
        val fragment: Fragment = SettingsFragment()
        val fragmentManager = fragmentActivityContext.supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.settings_frame, fragment)
            .setReorderingAllowed(true)
            .commit()
    }

    private fun onClickSymbolsColorButton(heartsColorButton: AppCompatButton) {
        if (!ftp.useEmoji) {

            if (!ftp.useEmoji) {
                context?.let {
                    ColorPickerPopup.Builder(it).initialColor(ftp.symbolsColor)
                        .enableBrightness(true)
                        .enableAlpha(true)
                        .okTitle(resources.getString(R.string.select))
                        .cancelTitle(resources.getString(R.string.cancel))
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(
                            object : ColorPickerPopup.ColorPickerObserver() {
                                override fun onColorPicked(color: Int) {
                                    heartsColorButton.setBackgroundColor(color)
                                    ftp.symbolsColor = color
                                }
                            })
                }
            }
        }
    }

    private fun activateDeactivateSymbolColorButton(view: View, activate: Boolean) {
        val defaultTextColor: Int = if (context != null) Utilities.getTextColorId(requireContext()) else Color.BLACK
        val emojiText = view.findViewById<TextView>(R.id.emojiText)
        emojiText.setTextColor(if (activate) Color.GRAY else defaultTextColor)
        val edgeShapeText = view.findViewById<TextView>(R.id.edgeShapeText)
        edgeShapeText.setTextColor(if (activate) defaultTextColor else Color.GRAY)
        val shapeColorText = view.findViewById<TextView>(R.id.shapeColorText)
        shapeColorText.setTextColor(if (activate) defaultTextColor else Color.GRAY)
        if (context != null && requireContext().resources != null) {
            val emojiButtonFrame = view.findViewById<View>(R.id.emojiButtonFrame)
            emojiButtonFrame.setBackgroundColor(
                if (!activate) ContextCompat.getColor(requireContext(), R.color.highlightBlue) else
                    ContextCompat.getColor(requireContext(), R.color.midDayFog)
            )
            val filledShapeButtonFrame = view.findViewById<View>(R.id.filledShapeButtonFrame)
            filledShapeButtonFrame.setBackgroundColor(
                if (activate) ContextCompat.getColor(requireContext(), R.color.highlightBlue) else
                    ContextCompat.getColor(requireContext(), R.color.midDayFog
                    )
            )
            val heartColorButtonFrame = view.findViewById<View>(R.id.shapeColorButtonFrame)
            heartColorButtonFrame.setBackgroundColor(
                if (activate) ContextCompat.getColor(requireContext(), R.color.highlightBlue) else ContextCompat.getColor(requireContext(),
                    R.color.midDayFog
                )
            )
            val unfilledShapeButtonFrame = view.findViewById<View>(R.id.unfilledShapeButtonFrame)
            unfilledShapeButtonFrame.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlightBlue))
        }
        emojiButton.setIsActive(!activate)
        filledShapeButton.setIsActive(activate)
        useEmojiSwitch = !activate
        val symbolsColorButton = view.findViewById<AppCompatButton>(R.id.symbolsColorButton)
        symbolsColorButton.setBackgroundColor(if (activate) ftp.symbolsColor else ftp.symbolsColor and -0x77000001)
    }

    private fun openEmojiPopup() {
        if (!useEmojiSwitch) {
            return
        }
        initializePopUpPoints()
        val alertDialog = Dialog(this.requireContext())
        val etc = this.context?.let { EmojiTableCtrl(it, purchasedMoreEmojis) }
        emojiButton.let { etc?.setSelectedEmojiCtrl(it) }
        alertDialog.window.let {
            it?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            if (etc != null) {
                alertDialog.setContentView(etc)
            }
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(it?.attributes)
            layoutParams.gravity = Gravity.TOP or Gravity.START
            layoutParams.x = 0
            layoutParams.y = 0
            layoutParams.width = mainShapePopupPt.x
            layoutParams.height = mainShapePopupPt.y
            it?.attributes = layoutParams
            etc?.setOnClickListener {
                emojiPopupReceivedClick(
                    alertDialog
                )
            }
            alertDialog.show()
        }
    }

    private fun emojiPopupReceivedClick(alertDialog: Dialog) {
        alertDialog.dismiss()
        ftp.emoji = emojiButton.emoji
    }

    private fun openShapePopup() {
        if (useEmojiSwitch) {
            return
        }
        initializePopUpPoints()
        val alertDialog = Dialog(this.requireContext())
        //  ([ShapeType, String])[] shapeTypeArray = {[ShapeType.StraightHeart, 0], ShapeType.Circle, ShapeType.Square, ShapeType.Star, ShapeType.Spade, ShapeType.Club, ShapeType.Diamond};
        if (this.context != null) {
            val stc = ShapeTableCtrl(this.requireContext(), purchasedMoreSymbols)

            filledShapeButton.let { stc.setSelectedShapeCtrl(it) }
            alertDialog.window.let {
                it?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                it?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                alertDialog.setContentView(stc)
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(it?.attributes)
                layoutParams.gravity = Gravity.TOP or Gravity.START
                layoutParams.x = shapePopUpPt.x
                layoutParams.y = shapePopUpPt.y
                it?.attributes = layoutParams
                stc.setOnClickListener {
                    openShapePopupReceivedClick(
                        alertDialog
                    )
                }
                alertDialog.show()
            }
        }
    }

    private fun openShapePopupReceivedClick(alertDialog: Dialog) {
        alertDialog.dismiss()
        ftp.symbolShapeType = filledShapeButton.getShapeType()
        ftp.symbol = filledShapeButton.getSymbol()
        resetMinDistEdgeShape()
    }

    private fun openMainShapePopup() {
        initializePopUpPoints()
        val alertDialog = Dialog(this.requireContext())
        val shapeTypeArray = arrayOf(MainShapeType.Heart, MainShapeType.Circle, MainShapeType.Square, MainShapeType.Diamond)
        val mSTC = this.context?.let { MainShapeTableCtrl(it, shapeTypeArray, unfilledShapeButton) }
        alertDialog.window.let {
            it?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            if (mSTC != null) {
                alertDialog.setContentView(mSTC)
            }
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(it?.attributes)
            layoutParams.gravity = Gravity.TOP or Gravity.START
            layoutParams.x = 0
            layoutParams.y = 0
            layoutParams.width = mainShapePopupPt.x
            layoutParams.height = mainShapePopupPt.y
            it?.attributes = layoutParams
            mSTC?.setOnClickListener {
                openMainShapePopupReceivedClick(
                    alertDialog
                )
            }
            alertDialog.show()
        }
    }

    private fun openMainShapePopupReceivedClick(alertDialog: Dialog) {
        alertDialog.dismiss()
        ftp.mainShapeType = unfilledShapeButton.getShapeType()
        resetMinDistEdgeShape()
    }

    private fun resetMinDistEdgeShape() {
        ftp.minDistEdgeShape = Utilities.closestDistance(
            ftp.useEmoji,
            ftp.emoji,
            ftp.symbol,
            ftp.symbolShapeType
        )

        val str = ftp.minDistEdgeShape.toString()
        minDistSymbols.setText(str)

        warningMsg.visibility = View.GONE
    }
}
