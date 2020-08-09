package com.zygne.bluetooth.presentation.ui

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Switch
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zygne.bluetooth.R
import com.zygne.bluetooth.domain.base.IDevice
import com.zygne.bluetooth.domain.implementation.BluetoothDeviceManager
import com.zygne.bluetooth.domain.implementation.DeviceFilterManager
import com.zygne.bluetooth.presentation.presenter.base.IBluetoothDevicePresenter
import com.zygne.bluetooth.presentation.presenter.implementation.BluetoothDevicePresenter
import kotlinx.android.synthetic.main.dialog_bluetooth_settings.*

internal class BluetoothDialog(
    activity: Activity,
    bluetoothManager: BluetoothDeviceManager,
    filterManager: DeviceFilterManager
) : Dialog(activity),
    DeviceAdapter.Callback,
    IBluetoothDevicePresenter.View {

    private var discoveryProgressView: LinearLayout
    private var btnStartDiscovery: Switch
    private var btnActivateBluetooth: Switch
    private var recyclerView: RecyclerView

    private var bluetoothDeviceAdapter: DeviceAdapter? = null
    private val bluetoothDeviceList: MutableList<IDevice> = mutableListOf()
    private var presenter: IBluetoothDevicePresenter

    init {
        presenter = BluetoothDevicePresenter(this, bluetoothManager, filterManager)

        setContentView(R.layout.dialog_bluetooth_settings)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        iv_close_bluetooth_dialog.setOnClickListener { dismiss() }

        discoveryProgressView = ll_discovery_progress_view
        btnStartDiscovery = btn_start_discovery
        btnStartDiscovery.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> startDiscovery()
                else -> stopDiscovery()
            }
        }

        btnActivateBluetooth = btn_activate_bluetooth
        btnActivateBluetooth.setOnCheckedChangeListener { _, isChecked ->
            when {
                isChecked -> presenter.activate()
                else -> presenter.deactivate()
            }
        }

        bluetoothDeviceAdapter =
            DeviceAdapter(
                activity,
                bluetoothDeviceList,
                this
            )
        recyclerView = rv_bluetooth_devices
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = bluetoothDeviceAdapter

        presenter.start()
    }

    private fun startDiscovery() {
        presenter.startDeviceLookUp()
    }

    private fun stopDiscovery() {
        presenter.stopDeviceLookUp()
    }

    override fun onAttachSelected(position: Int) {
        presenter.createBond(position)
    }

    override fun onDetachSelected(position: Int) {
        presenter.removeBond(position)
    }

    override fun updateDeviceList(devices: List<IDevice>) {
        bluetoothDeviceList.clear()
        bluetoothDeviceList.addAll(devices)
        bluetoothDeviceAdapter?.notifyDataSetChanged()
    }

    override fun deviceLookUpStopped() {
        ll_discovery_progress_view.visibility = View.INVISIBLE
        btnStartDiscovery.isChecked = false
    }

    override fun deviceLookUpStarted() {
        ll_discovery_progress_view.visibility = View.VISIBLE
    }

    override fun onActive() {
        btnActivateBluetooth.isChecked = true
    }

    override fun onInactive() {
        btnActivateBluetooth.isChecked = false
    }
}
