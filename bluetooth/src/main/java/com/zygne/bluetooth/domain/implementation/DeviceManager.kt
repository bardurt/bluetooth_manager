package com.zygne.bluetooth.domain.implementation

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.zygne.bluetooth.domain.base.IDeviceManager
import com.zygne.bluetooth.domain.base.IDeviceManager.Commands.REMOVE_BOND

class DeviceManager(
    private var context: Activity
) : IDeviceManager {

    private var listener: IDeviceManager.Listener? = null

    private val bluetoothFilter: IntentFilter = IntentFilter().apply {
        addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        addAction(BluetoothDevice.ACTION_FOUND)
        addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
    }

    private val bluetoothAdapter: BluetoothAdapter? get() = BluetoothAdapter.getDefaultAdapter()

    private var unBondedDevices: MutableList<com.zygne.bluetooth.domain.IDevice> = mutableListOf()
    private var bondedDevices: MutableList<com.zygne.bluetooth.domain.IDevice> = mutableListOf()

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Log.d(TAG, "Event received : " + action!!)
            // When discovery finds a device
            when {
                BluetoothDevice.ACTION_FOUND == action -> {
                    // Get the BluetoothDevice object from the Intent
                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    Log.d(TAG, "Device found : " + device.address)
                    // If it's already paired, skip it, because it's been listed already
                    if (device.bondState != BluetoothDevice.BOND_BONDED) {
                        Log.d(TAG, "UUIDS = ${device.uuids}")
                        Log.d(TAG, "type = ${device.type}")
                        Log.d(TAG, "describeContents = ${device.describeContents()}")
                        Log.d(TAG, "bluetoothClass = ${device.bluetoothClass}")
                        Log.d(TAG, "deviceClass = ${device.bluetoothClass.deviceClass}")
                        Log.d(TAG, "majorDeviceClass = ${device.bluetoothClass.majorDeviceClass}")
                        Log.d(TAG, "name = ${device.name}")
                        if (!unBondedDevices.contains(com.zygne.bluetooth.domain.IDevice(device))) {
                            unBondedDevices.add(com.zygne.bluetooth.domain.IDevice(device))
                        }

                        listener?.onNewDeviceFound(com.zygne.bluetooth.domain.IDevice(device))
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action -> listener?.onDeviceLookUpFinished()
                BluetoothAdapter.ACTION_DISCOVERY_STARTED == action -> listener?.onDeviceLookUpStarted()
                BluetoothAdapter.ACTION_STATE_CHANGED == action -> onActionStateChanged(intent)
                BluetoothDevice.ACTION_BOND_STATE_CHANGED == action -> {

                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0)

                    if (BluetoothDevice.BOND_BONDED == bondState) {
                        unBondedDevices.remove(com.zygne.bluetooth.domain.IDevice(device))
                        listener?.onDeviceConnected(com.zygne.bluetooth.domain.IDevice(device))
                    } else if (BluetoothDevice.BOND_NONE == bondState) {
                        unBondedDevices.add(com.zygne.bluetooth.domain.IDevice(device))
                        listener?.onDeviceDisconnected(com.zygne.bluetooth.domain.IDevice(device))
                    }
                }
            }
        }
    }

    init {
        context.registerReceiver(bluetoothReceiver, bluetoothFilter)
    }

    private fun onActionStateChanged(intent: Intent) {
        val currentState = intent.extras?.getInt(BluetoothAdapter.EXTRA_STATE)
        val previousState = intent.extras?.getInt(BluetoothAdapter.EXTRA_PREVIOUS_STATE)
        if (currentState == previousState) {
            return
        }
    }

    override fun setListener(listener: IDeviceManager.Listener) {
        this.listener = listener
    }

    override fun isBluetoothOn(): Boolean = bluetoothAdapter?.isEnabled ?: false

    override fun turnOn(): Boolean {
        bluetoothAdapter?.let {
            it.enable()
            return true
        }
        return false
    }

    override fun getConnectedDevices(): MutableList<com.zygne.bluetooth.domain.base.IDevice> {
        bondedDevices.clear()

        bluetoothAdapter?.let {
            for (device in it.bondedDevices) {
                bondedDevices.add(com.zygne.bluetooth.domain.IDevice(device))
            }
        }

        return bondedDevices.toMutableList()
    }

    override fun getNewDevices(): MutableList<com.zygne.bluetooth.domain.base.IDevice> {
        return unBondedDevices.toMutableList()
    }

    override fun startDiscovery() {
        bluetoothAdapter?.let {
            if (it.isDiscovering) {
                it.cancelDiscovery()
            }
            it.startDiscovery()
        }
    }

    override fun stopDiscovery() {
        bluetoothAdapter?.cancelDiscovery()
    }

    override fun disconnectDevice(address: String): Boolean {
        bluetoothAdapter?.let {
            if (it.isDiscovering) {
                Log.d(TAG, "Bluetooth cancelling discovery.")
                it.cancelDiscovery()
            }
        }

        var iDevice: com.zygne.bluetooth.domain.IDevice? = null
        getConnectedDevices()
        for (device in bondedDevices) {
            if (device.address == address) {
                iDevice = device
            }
        }

        if (iDevice == null) {
            return false
        }

        var success = true
        try {
            val bluetoothDevice = iDevice.bluetoothDevice
            BluetoothDevice::class.java.getMethod(REMOVE_BOND).invoke(bluetoothDevice)
        } catch (e: Exception) {
            Log.d(TAG, "Unpairing has failed. ${e.message}")
            success = false
        }

        return success
    }

    override fun connectDevice(address: String): Boolean {
        bluetoothAdapter?.let {
            if (it.isDiscovering) {
                Log.d(TAG, "Bluetooth cancelling discovery.")
                it.cancelDiscovery()
            }
        }

        var iDevice: com.zygne.bluetooth.domain.IDevice? = null
        for (device in unBondedDevices) {
            if (device.address == address) {
                iDevice = device
            }
        }

        val result = iDevice?.bluetoothDevice?.createBond()

        return true
    }

    override fun turnOff(): Boolean {
        bluetoothAdapter?.let {
            if (it.isDiscovering) {
                it.cancelDiscovery()
            }
            it.disable()
            return true
        }
        return false
    }

    override fun supportsFeature(): Boolean = bluetoothAdapter != null

    override fun destroy() {
        context.let {
            try {
                it.unregisterReceiver(bluetoothReceiver)
            } catch (ex: Exception) {
                // ignore, unregistered
            }
        }

        if (listener != null) {
            listener = null
        }

        unBondedDevices.clear()
    }

    companion object {
        private val TAG = DeviceManager::class.java.simpleName
    }
}
