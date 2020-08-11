package com.zygne.bluetooth.presentation.presenter.implementation

import com.zygne.bluetooth.domain.base.IDevice
import com.zygne.bluetooth.domain.base.IDeviceManager
import com.zygne.bluetooth.domain.base.IVendorMapper
import com.zygne.bluetooth.domain.implementation.BluetoothDeviceManager
import com.zygne.bluetooth.domain.implementation.DeviceFilterManager
import com.zygne.bluetooth.domain.implementation.VendorMapper
import com.zygne.bluetooth.presentation.presenter.base.IBluetoothDevicePresenter

internal class BluetoothDevicePresenter(
    private val view: IBluetoothDevicePresenter.View,
    private val bluetoothManager: BluetoothDeviceManager,
    private val filterManager: DeviceFilterManager,
    private val vendorMapper: IVendorMapper = VendorMapper()
) : IBluetoothDevicePresenter, IDeviceManager.Listener, IDeviceManager.ConnectionListener {

    private var startLookupOnActivation = false

    private val bluetoothDeviceList: MutableList<com.zygne.bluetooth.domain.base.IDevice> =
        mutableListOf()

    override fun start() {
        bluetoothManager.setListener(this)

        bluetoothManager.setConnectionListener(this)

        if (bluetoothManager.isActive()) {
            view.onActive()
        } else {
            view.onInactive()
        }

        bluetoothDeviceList.addAll(bluetoothManager.getConnectedDevices())

        prepareDevices()
    }

    override fun activate() {
        bluetoothManager.activate()
    }

    override fun deactivate() {
        bluetoothManager.deactivate()
    }

    override fun startDeviceLookUp() {
        if (!bluetoothManager.isActive()) {
            startLookupOnActivation = true
            bluetoothManager.activate()
        }
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
        startLookupOnActivation = false
    }

    override fun onNewDeviceFound(device: IDevice) {

        bluetoothDeviceList.clear()
        bluetoothDeviceList.addAll(bluetoothManager.getConnectedDevices())
        bluetoothDeviceList.addAll(bluetoothManager.getNewDevices())

        prepareDevices()
    }

    override fun onDeviceConnected(device: IDevice) {
        bluetoothDeviceList.clear()
        bluetoothDeviceList.addAll(bluetoothManager.getConnectedDevices())
        bluetoothDeviceList.addAll(bluetoothManager.getNewDevices())

        prepareDevices()
    }

    override fun onDeviceDisconnected(device: IDevice) {
        bluetoothDeviceList.clear()
        bluetoothDeviceList.addAll(bluetoothManager.getConnectedDevices())
        bluetoothDeviceList.addAll(bluetoothManager.getNewDevices())

        prepareDevices()
    }

    override fun onConnectionStateChanged(state: IDeviceManager.ConnectionState) {
        if (state == IDeviceManager.ConnectionState.Active) {
            view.onActive()
            if (startLookupOnActivation) {
                bluetoothManager.startDiscovery()
            }
        } else {
            view.onInactive()
        }
    }

    private fun prepareDevices() {
        filterManager.filterDevices(bluetoothDeviceList)

        for (item in bluetoothDeviceList) {
            vendorMapper.mapVendorToDevice(item)
        }

        view.updateDeviceList(bluetoothDeviceList)
    }
}
