package com.zygne.bluetooth.domain.implementation

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.zygne.bluetooth.domain.BTDevice
import com.zygne.bluetooth.domain.base.IBluetoothManager
import com.zygne.bluetooth.domain.base.IBluetoothManager.Commands.REMOVE_BOND

class BluetoothManager(
    private var context: Activity,
    listener: IBluetoothManager.IBluetoothListener
) : IBluetoothManager {

    private var listener: IBluetoothManager.IBluetoothListener? = null

    private val bluetoothFilter: IntentFilter = IntentFilter().apply {
        addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        addAction(BluetoothDevice.ACTION_FOUND)
        addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
    }

    private val bluetoothAdapter: BluetoothAdapter? get() = BluetoothAdapter.getDefaultAdapter()

    private var unBondedDevices: MutableList<BTDevice> = mutableListOf()

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
                        if(!unBondedDevices.contains(BTDevice(device))) {
                            unBondedDevices.add(BTDevice(device))
                        }

                        listener.onNewDeviceFound(BTDevice(device))
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action -> listener.onDiscoveryFinished()
                BluetoothAdapter.ACTION_DISCOVERY_STARTED == action -> listener.onDiscoveryStarted()
                BluetoothAdapter.STATE_ON.toString() == action -> listener.onBluetoothStateChange(1)
                BluetoothAdapter.STATE_OFF.toString() == action -> listener.onBluetoothStateChange(
                    0
                )
                BluetoothAdapter.ACTION_STATE_CHANGED == action -> onActionStateChanged(intent)
                BluetoothDevice.ACTION_BOND_STATE_CHANGED == action -> {

                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0)

                    if (BluetoothDevice.BOND_BONDED == bondState) {
                        unBondedDevices.remove(BTDevice(device))
                        listener.onDeviceBonded(BTDevice(device))
                    } else if (BluetoothDevice.BOND_NONE == bondState) {
                        unBondedDevices.add(BTDevice(device))
                        listener.onDeviceUnBonded(BTDevice(device))
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
            listener?.onBluetoothStateChange(currentState)
        }
    }

    override fun setListener(listener: IBluetoothManager.IBluetoothListener) {
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

    override fun getBondedDevices(): MutableList<BTDevice> {
        val deviceList = mutableListOf<BTDevice>()

        bluetoothAdapter?.let {
            for(device in it.bondedDevices){
                deviceList.add(BTDevice(device))
            }
        }

        return deviceList
    }

    override fun getUnbondedDevices(): MutableList<BTDevice> {
        return unBondedDevices
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

    override fun removeBond(device: BTDevice): Boolean {
        var success = true
        try {
            val bluetoothDevice = device.bluetoothDevice
            BluetoothDevice::class.java.getMethod(REMOVE_BOND).invoke(bluetoothDevice)
        } catch (e: Exception) {
            Log.d(TAG, "Unpairing has failed. ${e.message}")
            success = false
        }

        return success
    }

    override fun createBond(device: BTDevice): Boolean {
        bluetoothAdapter?.let {
            if (it.isDiscovering) {
                Log.d(TAG, "Bluetooth cancelling discovery.")
                it.cancelDiscovery()
            }
        }

        val result = device.bluetoothDevice.createBond()

        Log.d(TAG, "Create bond to : " + device.address + " = " + result)
        return result
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

    override fun supportsBluetooth(): Boolean = bluetoothAdapter != null

    override fun destroy() {
        context.let {
            try {
                it.unregisterReceiver(bluetoothReceiver)
            } catch (ex: Exception) {
                //ignore, unregistered
            }
        }

        if (listener != null) {
            listener = null
        }

        unBondedDevices.clear()
    }


    companion object {
        private val TAG = BluetoothManager::class.java.simpleName
    }
}
