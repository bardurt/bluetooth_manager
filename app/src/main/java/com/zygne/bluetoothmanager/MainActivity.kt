package com.zygne.bluetoothmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zygne.bluetooth.BluetoothModule
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothModule: BluetoothModule

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        bluetoothModule = BluetoothModule(this)
        bluetoothModule.start()

        btn_bluetooth.setOnClickListener {
            bluetoothModule.showBluetoothDialog()
        }
    }
}
