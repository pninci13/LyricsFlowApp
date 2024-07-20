package com.example.lyricsflowapp.ui.helpers

import android.app.Activity
import android.app.AlertDialog
import android.widget.Button
import android.widget.TextView
import com.example.lyricsflowapp.R

object AlertHelper {
    fun showAlertDialog(activity: Activity, message: String) {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_alert_dialog, null)
        builder.setView(dialogView)

        val tvMessage = dialogView.findViewById<TextView>(R.id.alertDialogMessage)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

        tvMessage.text = message

        val alertDialog = builder.create()

        btnOk.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()

        // Set background to transparent and dim amount
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.window?.setDimAmount(0.7f) // Adjust the dim amount to your preference
    }

    fun showSuccessDialog(activity: Activity, title: String, message: String, onConfirm: () -> Unit) {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_success_alert_dialog, null)
        builder.setView(dialogView)

        val tvTitle = dialogView.findViewById<TextView>(R.id.successTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.successMessage)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOkay)

        tvTitle.text = title
        tvMessage.text = message

        val alertDialog = builder.create()

        btnOk.setOnClickListener {
            alertDialog.dismiss()
            onConfirm()
        }

        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
