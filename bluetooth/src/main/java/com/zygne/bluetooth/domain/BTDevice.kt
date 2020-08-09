package com.zygne.bluetooth.domain

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.BOND_BONDED
import com.zygne.bluetooth.domain.base.Connection
import com.zygne.bluetooth.domain.base.IDevice

class BTDevice(val bluetoothDevice: BluetoothDevice) :
    IDevice {

    private var vendorName = ""

    override val name: String
        get() = bluetoothDevice.name ?: "UNKNOWN NAME"

    override val address: String
        get() = bluetoothDevice.address ?: "UNKNOWN ADDRESS"

    override val oui: String
        get() = bluetoothDevice.address.take(8)

    override val connection: Connection
        get() = Connection.Bluetooth

    override fun attached(): Boolean {
        return bluetoothDevice.bondState == BOND_BONDED
    }

    override var vendor: String
        get() = vendorName
        set(value) {
            vendorName = value
        }

    override fun equals(other: Any?): Boolean {

        if (other == null) {
            return false
        }
        if (other !is com.zygne.bluetooth.domain.BTDevice) {
            return false
        }

        return bluetoothDevice == other.bluetoothDevice
    }
}
