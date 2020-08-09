package com.zygne.bluetooth.domain.base

/**
 * Created by Bardur Thomsen on 3/28/19.
 *
 * Interface for wrapping the functionality of the Android Bluetooth module.
 * Usually an implementation will rely on the Android's Context, so it's pretty important
 * to call the onDestroy() method when finishing in order to avoid memory leaks.
 */
interface IDeviceManager {

    /**
     * get list of all devices which are BONDED to this devices
     */
    fun getConnectedDevices(): MutableList<IDevice>

    /**
     * get list of all devices which are not BONDED
     */
    fun getNewDevices(): MutableList<IDevice>

    /**
     * check if bluetooth is activated
     */
    fun isActive(): Boolean

    /**
     * set listener to get updates
     */
    fun setListener(listener: Listener)

    fun setConnectionListener(listener: ConnectionListener)

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
    fun connectDevice(device: IDevice): Boolean

    /**
     * Remove the bond
     */
    fun disconnectDevice(device: IDevice): Boolean

    /**
     * turn on bluetooth
     */
    fun activate(): Boolean

    /**
     *     turn off bluetooth
     */
    fun deactivate(): Boolean

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

    interface ConnectionListener {
        fun onConnectionStateChanged(state: ConnectionState)
    }

    enum class ConnectionState {
        Active,
        Inactive
    }
}
