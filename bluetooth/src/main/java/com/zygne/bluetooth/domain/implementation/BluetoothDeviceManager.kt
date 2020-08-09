package com.zygne.bluetooth.domain.implementation

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.STATE_OFF
import android.bluetooth.BluetoothAdapter.STATE_ON
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.zygne.bluetooth.domain.BTDevice
import com.zygne.bluetooth.domain.base.Connection
import com.zygne.bluetooth.domain.base.IDevice
import com.zygne.bluetooth.domain.base.IDeviceManager
import java.lang.RuntimeException

class BluetoothDeviceManager(
    private var context: Activity
) : IDeviceManager {

    private var listener: IDeviceManager.Listener? = null
    private var connectionListener: IDeviceManager.ConnectionListener? = null

    private val bluetoothFilter: IntentFilter = IntentFilter().apply {
        addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        addAction(BluetoothDevice.ACTION_FOUND)
        addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
    }

    private val bluetoothAdapter: BluetoothAdapter? get() = BluetoothAdapter.getDefaultAdapter()

    private var unBondedDevices: MutableList<com.zygne.bluetooth.domain.BTDevice> = mutableListOf()
    private var bondedDevices: MutableList<com.zygne.bluetooth.domain.BTDevice> = mutableListOf()

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
                        if (!unBondedDevices.contains(com.zygne.bluetooth.domain.BTDevice(device))) {
                            unBondedDevices.add(com.zygne.bluetooth.domain.BTDevice(device))
                        }

                        listener?.onNewDeviceFound(com.zygne.bluetooth.domain.BTDevice(device))
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
                        unBondedDevices.remove(com.zygne.bluetooth.domain.BTDevice(device))
                        listener?.onDeviceConnected(com.zygne.bluetooth.domain.BTDevice(device))
                    } else if (BluetoothDevice.BOND_NONE == bondState) {
                        unBondedDevices.add(com.zygne.bluetooth.domain.BTDevice(device))
                        listener?.onDeviceDisconnected(com.zygne.bluetooth.domain.BTDevice(device))
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

        if (currentState != null) {
            if (currentState == STATE_OFF) {
                connectionListener?.onConnectionStateChanged(IDeviceManager.ConnectionState.Inactive)
            } else if (currentState == STATE_ON) {
                connectionListener?.onConnectionStateChanged(IDeviceManager.ConnectionState.Active)
            }
        }
    }

    override fun setListener(listener: IDeviceManager.Listener) {
        this.listener = listener
    }

    override fun setConnectionListener(listener: IDeviceManager.ConnectionListener) {
        this.connectionListener = listener
    }

    override fun isActive(): Boolean = bluetoothAdapter?.isEnabled ?: false

    override fun activate(): Boolean {
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
                bondedDevices.add(com.zygne.bluetooth.domain.BTDevice(device))
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

    override fun disconnectDevice(device: IDevice): Boolean {
        bluetoothAdapter?.let {
            if (it.isDiscovering) {
                Log.d(TAG, "Bluetooth cancelling discovery.")
                it.cancelDiscovery()
            }
        }

        if (device.connection != Connection.Bluetooth) {
            throw RuntimeException("Device is not a bluetooth device")
        }

        var btDevice: BTDevice? = null
        getConnectedDevices()
        for (item in bondedDevices) {
            if (item.address == device.address) {
                btDevice = item
            }
        }

        if (btDevice == null) {
            return false
        }

        var success = true
        try {
            val bluetoothDevice = btDevice.bluetoothDevice
            BluetoothDevice::class.java.getMethod(REMOVE_BOND).invoke(bluetoothDevice)
        } catch (e: Exception) {
            Log.d(TAG, "Unpairing has failed. ${e.message}")
            success = false
        }

        return success
    }

    override fun connectDevice(device: IDevice): Boolean {
        bluetoothAdapter?.let {
            if (it.isDiscovering) {
                Log.d(TAG, "Bluetooth cancelling discovery.")
                it.cancelDiscovery()
            }
        }

        if (device.connection != Connection.Bluetooth) {
            throw RuntimeException("Device is not a bluetooth device")
        }

        var btDevice: com.zygne.bluetooth.domain.BTDevice? = null
        for (item in unBondedDevices) {
            if (item.address == device.address) {
                btDevice = item
            }
        }

        val result = btDevice?.bluetoothDevice?.createBond()

        return true
    }

    override fun deactivate(): Boolean {
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
        private val TAG = BluetoothDeviceManager::class.java.simpleName
        private const val CREATE_BOND = "createBond"
        private const val REMOVE_BOND = "removeBond"
    }
}
