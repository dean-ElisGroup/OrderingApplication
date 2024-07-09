package com.elis.orderingapplication.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.elis.orderingapplication.R

object DeviceInfoDialog {
    fun showAlertDialog(
        context: Context,
        message: String
    ) {
        val builder = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.device_info_dialog, null)
        val titleTextView = dialogView.findViewById<TextView>(R.id.title_text_view)
        val messageTextView = dialogView.findViewById<TextView>(R.id.message_text_view)
        val positiveButton = dialogView.findViewById<Button>(R.id.positive_button)
        titleTextView.text = "Device info"
        messageTextView.text = message
        positiveButton.text = "OK"

        builder.setView(dialogView)

        val dialog = builder.create()
        positiveButton.setOnClickListener { dialog.dismiss() }
        dialog.show()


        /*val dialog = AlertDialog.Builder(context, R.style.CustomAlertDialogStyle)
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ -> }
            .create()
        dialog.show()*/
    }
}
