package com.zygne.bluetooth.presentation.presenter.implementation

import com.zygne.bluetooth.domain.base.IDeviceManager
import com.zygne.bluetooth.domain.implementation.DeviceFilterManager
import com.zygne.bluetooth.domain.implementation.DeviceManager
import com.zygne.bluetooth.presentation.presenter.base.BluetoothDevicePresenter

class BluetoothDevicePresenterImpl(
    private val view: BluetoothDevicePresenter.View,
    private val bluetoothManager: DeviceManager,
    private val filterManager: DeviceFilterManager
) : BluetoothDevicePresenter, IDeviceManager.Listener {

    private val bluetoothDeviceList: MutableList<com.zygne.bluetooth.domain.base.IDevice> = mutableListOf()

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
        bluetoothManager.connectDevice(bluetoothDeviceList[position].address)
    }

    override fun removeBond(position: Int) {
        bluetoothManager.disconnectDevice(bluetoothDeviceList[position].address)
    }

    override fun onDeviceLookUpStarted() {
        view.deviceLookUpStarted()
    }

    override fun onDeviceLookUpFinished() {
        view.deviceLookUpStopped()
    }

    override fun onNewDeviceFound(device: com.zygne.bluetooth.domain.IDevice) {

        bluetoothDeviceList.clear()
        bluetoothDeviceList.addAll(bluetoothManager.getConnectedDevices())
        bluetoothDeviceList.addAll(bluetoothManager.getNewDevices())

        filterManager.filterDevices(bluetoothDeviceList)

        view.updateDeviceList(bluetoothDeviceList)
    }

    override fun onDeviceConnected(device: com.zygne.bluetooth.domain.IDevice) {
        bluetoothDeviceList.clear()
        bluetoothDeviceList.addAll(bluetoothManager.getConnectedDevices())
        bluetoothDeviceList.addAll(bluetoothManager.getNewDevices())

        filterManager.filterDevices(bluetoothDeviceList)

        view.updateDeviceList(bluetoothDeviceList)
    }

    override fun onDeviceDisconnected(device: com.zygne.bluetooth.domain.IDevice) {
        bluetoothDeviceList.clear()
        bluetoothDeviceList.addAll(bluetoothManager.getConnectedDevices())
        bluetoothDeviceList.addAll(bluetoothManager.getNewDevices())

        filterManager.filterDevices(bluetoothDeviceList)

        view.updateDeviceList(bluetoothDeviceList)
    }
}
