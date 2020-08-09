package com.zygne.bluetooth.presentation.presenter.implementation

import com.zygne.bluetooth.domain.base.IDevice
import com.zygne.bluetooth.domain.base.IDeviceManager
import com.zygne.bluetooth.domain.implementation.BluetoothDeviceManager
import com.zygne.bluetooth.domain.implementation.DeviceFilterManager
import com.zygne.bluetooth.presentation.presenter.base.IBluetoothDevicePresenter

internal class BluetoothDevicePresenter(
    private val view: IBluetoothDevicePresenter.View,
    private val bluetoothManager: BluetoothDeviceManager,
    private val filterManager: DeviceFilterManager
) : IBluetoothDevicePresenter, IDeviceManager.Listener {

    private val bluetoothDeviceList: MutableList<com.zygne.bluetooth.domain.base.IDevice> =
        mutableListOf()

    override fun start() {
        bluetoothManager.setListener(this)

        bluetoothManager.turnOn()

        bluetoothDeviceList.addAll(bluetoothManager.getConnectedDevices())

        filterManager.filterDevices(bluetoothDeviceList)

        view.updateDeviceList(bluetoothDeviceList)
    }

    override fun startDeviceLookUp() {
        bluetoothManager.startDiscovery()
    }

    override fun stopDeviceLookUp() {
        bluetoothManager.stopDiscovery()
    }

    override fun createBond(position: Int) {
        bluetoothManager.connectDevice(bluetoothDeviceList[position])
    }

    override fun removeBond(position: Int) {
        bluetoothManager.disconnectDevice(bluetoothDeviceList[position])
    }

    override fun onDeviceLookUpStarted() {
        view.deviceLookUpStarted()
    }

    override fun onDeviceLookUpFinished() {
        view.deviceLookUpStopped()
    }

    override fun onNewDeviceFound(device: IDevice) {

        bluetoothDeviceList.clear()
        bluetoothDeviceList.addAll(bluetoothManager.getConnectedDevices())
        bluetoothDeviceList.addAll(bluetoothManager.getNewDevices())

        filterManager.filterDevices(bluetoothDeviceList)

        view.updateDeviceList(bluetoothDeviceList)
    }

    override fun onDeviceConnected(device: IDevice) {
        bluetoothDeviceList.clear()
        bluetoothDeviceList.addAll(bluetoothManager.getConnectedDevices())
        bluetoothDeviceList.addAll(bluetoothManager.getNewDevices())

        filterManager.filterDevices(bluetoothDeviceList)

        view.updateDeviceList(bluetoothDeviceList)
    }

    override fun onDeviceDisconnected(device: IDevice) {
        bluetoothDeviceList.clear()
        bluetoothDeviceList.addAll(bluetoothManager.getConnectedDevices())
        bluetoothDeviceList.addAll(bluetoothManager.getNewDevices())

        filterManager.filterDevices(bluetoothDeviceList)

        view.updateDeviceList(bluetoothDeviceList)
    }
}
