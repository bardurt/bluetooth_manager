package com.zygne.bluetoothmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zygne.bluetooth.BluetoothModule
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothModule: BluetoothModule

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetoothModule = BluetoothModule(this)

        btn_bluetooth.setOnClickListener {
            bluetoothModule.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothModule.stop()
    }

    fun showBluetoothSettings() {
        bluetoothModule.start()
    }
}
