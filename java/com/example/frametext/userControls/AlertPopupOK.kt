package com.example.frametext.userControls

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.frametext.R
import com.example.frametext.userControls.colorPicker.Constants

class AlertPopupOK(private var title: String, private var message: String) {

    fun show(view: View, context: Context) {
        val popupView: View = View.inflate(context, R.layout.alert_popup, null)

        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT
        val focusable = true
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        val backgroundScreen: LinearLayout = popupView.findViewById(R.id.background_screen)
        backgroundScreen.setBackgroundColor(Constants.DARK_BACKGROUND_OPACITY)

        val titleTextView: TextView = popupView.findViewById(R.id.title)
        titleTextView.text = title

        val messageTextView: TextView = popupView.findViewById(R.id.message)
        messageTextView.text = message

        val okBtn: AppCompatButton = popupView.findViewById(R.id.ok)
        okBtn.setOnClickListener {
            popupWindow.dismiss()
        }

        val cancelBtn: AppCompatButton = popupView.findViewById(R.id.cancel)
        cancelBtn.visibility = View.GONE

        val rightOfCancelView: View = popupView.findViewById(R.id.right_of_cancel)
        rightOfCancelView.visibility = View.GONE
    }
}