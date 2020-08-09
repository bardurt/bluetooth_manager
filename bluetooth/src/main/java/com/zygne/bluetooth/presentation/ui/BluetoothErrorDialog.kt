package com.zygne.bluetooth.presentation.ui

import android.app.Activity
import android.app.Dialog
import com.zygne.bluetooth.R
import kotlinx.android.synthetic.main.dialog_bluetooth_error.*

internal class BluetoothErrorDialog(
    activity: Activity,
    errorMessage: String
) : Dialog(activity) {

    init {
        setContentView(R.layout.dialog_bluetooth_error)

        btn_dismiss_error_dialog.setOnClickListener { dismiss() }

        tv_error_dialog_message.text = errorMessage
    }
}
