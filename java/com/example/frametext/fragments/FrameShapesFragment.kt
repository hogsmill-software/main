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
    private var fragmentActivityContext: FragmentActivity? = null
    var ftp: FrameTextParameters? = null
    private var emojiButton: EmojiCellCtrl? = null
    private var filledShapeButton: ShapeCellCtrl? = null
    private var unfilledShapeButton: MainShapeCellCtrl? = null
    private var emojiPopUpPt: Point? = null
    private var shapePopUpPt: Point? = null
    private var mainShapePopupPt: Point? = null
    private var popUpPointsInitialized = false
    private var useEmojiSwitch = false
    private var minDistSymbols: EditText? = null
    private var warningMsg: TextView? = null
    private var newFeaturesViewModel: NewFeaturesViewModel? = null
    private var purchasedMoreEmojis = false
    private var purchasedMoreSymbols = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentActivityContext = context as FragmentActivity
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val heartValParametersViewModel = ViewModelProvider(requireActivity())[FrameTextParametersViewModel::class.java]
        ftp = heartValParametersViewModel.getSelectedItem().value

        val symbolsColorButton = view.findViewById<AppCompatButton>(R.id.symbolsColorButton)
        symbolsColorButton.setBackgroundColor(ftp!!.symbolsColor)
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
            newFeaturesViewModel!!.isPurchased(SKU_MORE_EMOJIS).observe(
                viewLifecycleOwner
            ) { purchasedMoreEmojis = it }

            newFeaturesViewModel!!.isPurchased(SKU_MORE_SYMBOLS).observe(
                viewLifecycleOwner
            ) { purchasedMoreSymbols = it }
        }

        val notPurchasedMoreEmojisMessage = view.findViewById<TextView>(R.id.notPurchasedMoreEmojisMessage)
        notPurchasedMoreEmojisMessage.visibility = if (purchasedMoreEmojis) View.GONE else View.VISIBLE

        val notPurchasedMoreSymbolsMessage = view.findViewById<TextView>(R.id.notPurchasedMoreSymbolsMessage)
        notPurchasedMoreSymbolsMessage.visibility = if (purchasedMoreSymbols) View.GONE else View.VISIBLE

        emojiButton = view.findViewById(R.id.emojiButton)
        emojiButton?.setEmoji(ftp!!.emoji)
        emojiButton?.setOnClickListener { _: View? -> openEmojiPopup() }

        filledShapeButton = view.findViewById(R.id.filledShapeButton)
        if (ftp!!.symbolShapeType == SymbolShapeType.None) {
            filledShapeButton?.setSymbol(ftp!!.symbol)
        } else {
            filledShapeButton?.setShapeType(ftp!!.symbolShapeType)
        }
        filledShapeButton?.setOnClickListener { _: View? -> openShapePopup() }

        activateDeactivateSymbolColorButton(view, !ftp!!.useEmoji)
        val useEmojiSwitch = view.findViewById<SwitchCompat>(R.id.useEmojiSwitch)

        useEmojiSwitch.isChecked = ftp!!.useEmoji
        useEmojiSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            ftp!!.useEmoji = isChecked
            activateDeactivateSymbolColorButton(view, !isChecked)
            resetMinDistEdgeShape()
        }

        unfilledShapeButton = view.findViewById(R.id.unfilledShapeButton)
        unfilledShapeButton?.setFillShape(false)
        unfilledShapeButton?.setShapeType(ftp!!.mainShapeType)
        unfilledShapeButton?.setOnClickListener { _: View? -> openMainShapePopup() }

        warningMsg = view.findViewById(R.id.warningMsg)
        minDistSymbols = view.findViewById(R.id.min_dist_symbols)

        minDistSymbols?.setText(
            java.lang.String.format(
                Locale.getDefault(),
                "%d",
                ftp!!.minDistEdgeShape,
            ), TextView.BufferType.EDITABLE
        )

        minDistSymbols?.filters = arrayOf<InputFilter>(MinMaxFilter(0, 500, ::assignMinDistEdgeShapeIfMoreThan100
        ) {   // If user has neglected to fill this value, reset this to default
            ftp!!.minDistEdgeShape = Utilities.closestDistance(
                ftp!!.useEmoji,
                ftp!!.emoji,
                ftp?.symbol,
                ftp!!.symbolShapeType
            )
            warningMsg!!.visibility = View.VISIBLE
            warningMsg!!.text = String.format(requireContext().resources!!.getString(R.string.blank_default_error_msg))
        })
    }

    private fun assignMinDistEdgeShapeIfMoreThan100(dist: Int) {
        if (dist >= 100) {
            ftp!!.minDistEdgeShape = dist
            warningMsg!!.visibility = View.GONE
        }
        else {
            warningMsg!!.visibility = View.VISIBLE
            warningMsg!!.text = String.format(requireContext().resources.getString(R.string.less_than_100_error_msg), dist)
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
            emojiPopUpPt!!.x = screenSizePt.x
            emojiPopUpPt!!.y = screenSizePt.y

            // now the shape button
            shapePopUpPt!!.x = screenSizePt.x
            shapePopUpPt!!.y = emojiPopUpPt!!.y

            // now the main shape popup button
            mainShapePopupPt!!.x = screenSizePt.x
            mainShapePopupPt!!.y = screenSizePt.y
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
        val fragmentManager = fragmentActivityContext!!.supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.settings_frame, fragment)
            .setReorderingAllowed(true)
            .commit()
    }

    private fun onClickSymbolsColorButton(heartsColorButton: AppCompatButton) {
        if (!ftp!!.useEmoji) {

            if (!ftp!!.useEmoji) {
                context?.let {
                    ColorPickerPopup.Builder(it).initialColor(ftp!!.symbolsColor)
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
                                    ftp!!.symbolsColor = color
                                }
                            })
                }
            }
        }
    }

    private fun activateDeactivateSymbolColorButton(view: View, activate: Boolean) {
        val emojiText = view.findViewById<TextView>(R.id.emojiText)
        emojiText.setTextColor(if (activate) Color.GRAY else Color.BLACK)
        val edgeShapeText = view.findViewById<TextView>(R.id.edgeShapeText)
        edgeShapeText.setTextColor(if (activate) Color.BLACK else Color.GRAY)
        val shapeColorText = view.findViewById<TextView>(R.id.shapeColorText)
        shapeColorText.setTextColor(if (activate) Color.BLACK else Color.GRAY)
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
        emojiButton!!.setIsActive(!activate)
        filledShapeButton!!.setIsActive(activate)
        useEmojiSwitch = !activate
        val symbolsColorButton = view.findViewById<AppCompatButton>(R.id.symbolsColorButton)
        symbolsColorButton.setBackgroundColor(if (activate) ftp!!.symbolsColor else ftp!!.symbolsColor and -0x77000001)
    }

    private fun openEmojiPopup() {
        if (!useEmojiSwitch) {
            return
        }
        initializePopUpPoints()
        val alertDialog = Dialog(this.requireContext())
        val etc = this.context?.let { EmojiTableCtrl(it, purchasedMoreEmojis) }
        etc?.setSelectedEmojiCtrl(emojiButton)
        alertDialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (etc != null) {
            alertDialog.setContentView(etc)
        }
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertDialog.window!!.attributes)
        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.x = 0
        layoutParams.y = 0
        layoutParams.width = mainShapePopupPt!!.x
        layoutParams.height = mainShapePopupPt!!.y
        alertDialog.window!!.attributes = layoutParams
        etc?.setOnClickListener {
            emojiPopupReceivedClick(
                alertDialog
            )
        }
        alertDialog.show()
    }

    private fun emojiPopupReceivedClick(alertDialog: Dialog) {
        alertDialog.dismiss()
        ftp!!.emoji = emojiButton!!.emoji
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

            filledShapeButton?.let { stc.setSelectedShapeCtrl(it) }
            alertDialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.setContentView(stc)
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(alertDialog.window!!.attributes)
            layoutParams.gravity = Gravity.TOP or Gravity.START
            layoutParams.x = shapePopUpPt!!.x
            layoutParams.y = shapePopUpPt!!.y
            alertDialog.window!!.attributes = layoutParams
            stc.setOnClickListener {
                openShapePopupReceivedClick(
                    alertDialog
                )
            }
            alertDialog.show()
        }
    }

    private fun openShapePopupReceivedClick(alertDialog: Dialog) {
        alertDialog.dismiss()
        ftp!!.symbolShapeType = filledShapeButton!!.getShapeType()
        ftp!!.symbol = filledShapeButton!!.getSymbol()
        resetMinDistEdgeShape()
    }

    private fun openMainShapePopup() {
        initializePopUpPoints()
        val alertDialog = Dialog(this.requireContext())
        val shapeTypeArray = arrayOf(MainShapeType.Heart, MainShapeType.Circle, MainShapeType.Square)
        val mSTC = this.context?.let { MainShapeTableCtrl(it, shapeTypeArray, false) }
        unfilledShapeButton?.let { mSTC?.setSelectedEmojiCtrl(it) }
        alertDialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (mSTC != null) {
            alertDialog.setContentView(mSTC)
        }
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertDialog.window!!.attributes)
        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.x = 0
        layoutParams.y = 0
        layoutParams.width = mainShapePopupPt!!.x
        layoutParams.height = mainShapePopupPt!!.y
        alertDialog.window!!.attributes = layoutParams
        mSTC?.setOnClickListener {
            openMainShapePopupReceivedClick(
                alertDialog
            )
        }
        alertDialog.show()
    }

    private fun openMainShapePopupReceivedClick(alertDialog: Dialog) {
        alertDialog.dismiss()
        ftp!!.mainShapeType = unfilledShapeButton!!.getShapeType()
        resetMinDistEdgeShape()
    }

    private fun resetMinDistEdgeShape() {
        ftp?.minDistEdgeShape = Utilities.closestDistance(
            ftp!!.useEmoji,
            ftp!!.emoji,
            ftp?.symbol,
            ftp!!.symbolShapeType
        )

        if (minDistSymbols != null && ftp?.minDistEdgeShape != null) {
            val str = ftp?.minDistEdgeShape!!.toString()
            minDistSymbols!!.setText(str)
        }

        if (warningMsg != null) {
            warningMsg!!.visibility = View.GONE
        }
    }
}
