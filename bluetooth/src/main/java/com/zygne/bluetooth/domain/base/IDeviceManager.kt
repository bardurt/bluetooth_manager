package com.zygne.bluetooth.domain.base

import com.zygne.bluetooth.domain.IDevice

/**
 * Created by Bardur Thomsen on 3/28/19.
 *
 * Interface for wrapping the functionality of the Android Bluetooth module.
 * Usually an implementation will rely on the Android's Context, so it's pretty important
 * to call the onDestroy() method when finishing in order to avoid memory leaks.
 */
interface IDeviceManager {

    /**
     * Commands for hacking the Bluetooth module.
     * Note : The android Bluetooth module is VERY buggy, some brands require some hack,
     * which use these commands.
     */
    companion object Commands {
        const val CREATE_BOND = "createBond"
        const val REMOVE_BOND = "removeBond"
    }

    /**
     * get list of all devices which are BONDED to this devices
     */
    fun getConnectedDevices(): MutableList<com.zygne.bluetooth.domain.base.IDevice>

    /**
     * get list of all devices which are not BONDED
     */
    fun getNewDevices(): MutableList<com.zygne.bluetooth.domain.base.IDevice>

    /**
     * check if bluetooth is activated
     */
    fun isBluetoothOn(): Boolean

    /**
     * set listener to get updates
     */
    fun setListener(listener: Listener)

    /**
     * Start bluetooth discovery process, look for bluetooth devices
     */
    fun startDiscovery()

    /**
     * Stop the bluetooth discovery process
     */
    fun stopDiscovery()

    /**
     * Start the pairing process with a bluetooth device
     */
    fun connectDevice(address: String): Boolean

    /**
     * Remove the bond
     */
    fun disconnectDevice(address: String): Boolean

    /**
     * turn on bluetooth
     */
    fun turnOn(): Boolean

    /**
     *     turn off bluetooth
     */
    fun turnOff(): Boolean

    /**
     * check if this devices supports bluetooth
     */
    fun supportsFeature(): Boolean

    /**
     * method for cleaning up
     */
    fun destroy()

    interface Listener {
        fun onDeviceLookUpStarted()
        fun onDeviceLookUpFinished()
        fun onNewDeviceFound(device: IDevice)
        fun onDeviceConnected(device: IDevice)
        fun onDeviceDisconnected(device: IDevice)
    }
}
