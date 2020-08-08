package com.zygne.bluetooth.view

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Switch
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zygne.bluetooth.R
import com.zygne.bluetooth.domain.BTDevice
import com.zygne.bluetooth.domain.base.IBluetoothManager
import com.zygne.bluetooth.domain.implementation.BluetoothManager
import kotlinx.android.synthetic.main.dialog_bluetooth_settings.*

class BluetoothDialog(
    activity: Activity
) : Dialog(activity),
    IBluetoothManager.IBluetoothListener,
    DeviceAdapter.Callback {


    private val bluetoothManager = BluetoothManager(activity, this)
    private var discoveryProgressView: LinearLayout
    private var btnStartDiscovery: Switch
    private var recyclerView: RecyclerView

    private var bluetoothDeviceAdapter: DeviceAdapter? = null
    private val bluetoothDeviceList: MutableList<BTDevice> = mutableListOf()

    init {
        setContentView(R.layout.dialog_bluetooth_settings)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        iv_close_bluetooth_dialog.setOnClickListener { dismiss() }
        bluetoothManager.setListener(this)

        discoveryProgressView = ll_discovery_progress_view
        btnStartDiscovery = btn_start_discovery
        btnStartDiscovery.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> startDiscovery()
                else -> stopDiscovery()
            }
        }

        bluetoothDeviceList.addAll(bluetoothManager.getBondedDevices())

        bluetoothDeviceAdapter = DeviceAdapter(
            activity,
            bluetoothDeviceList,
            this
        )
        recyclerView = rv_bluetooth_devices
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = bluetoothDeviceAdapter

        bluetoothManager.turnOn()
    }

    override fun onDiscoveryStarted() {
        discoveryProgressView.visibility = View.VISIBLE
    }

    override fun onDiscoveryFinished() {
        discoveryProgressView.visibility = View.INVISIBLE
        btnStartDiscovery.isChecked = false
    }

    override fun onDeviceBonded(bluetoothDevice: BTDevice) {
        bluetoothDeviceList.clear()
        bluetoothDeviceList.addAll(bluetoothManager.getBondedDevices())
        bluetoothDeviceList.addAll(bluetoothManager.getUnbondedDevices())
        bluetoothDeviceAdapter?.notifyDataSetChanged()
    }

    override fun onDeviceUnBonded(bluetoothDevice: BTDevice) {
        refreshList()
    }

    override fun onNewDeviceFound(bluetoothDevice: BTDevice) {
        refreshList()
    }

    override fun onBluetoothStateChange(state: Int) {
    }

    private fun startDiscovery() {
        bluetoothManager.turnOn()
        bluetoothManager.startDiscovery()
    }

    private fun stopDiscovery() {
        bluetoothManager.stopDiscovery()
    }

    override fun onAttachSelected(position: Int) {
        bluetoothManager.createBond(bluetoothDeviceList[position])
    }

    override fun onDetachSelected(position: Int) {
        bluetoothManager.removeBond(bluetoothDeviceList[position])
    }

    private fun refreshList() {
        bluetoothDeviceList.clear()
        bluetoothDeviceList.addAll(bluetoothManager.getBondedDevices())
        bluetoothDeviceList.addAll(bluetoothManager.getUnbondedDevices())
        bluetoothDeviceAdapter?.notifyDataSetChanged()
    }

}
