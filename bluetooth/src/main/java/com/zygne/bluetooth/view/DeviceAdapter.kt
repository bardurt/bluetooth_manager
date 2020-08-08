package com.zygne.bluetooth.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zygne.bluetooth.R
import com.zygne.bluetooth.domain.base.IDevice
import kotlinx.android.synthetic.main.item_device.view.*

internal class DeviceAdapter(
    private val context: Context,
    private val items: List<IDevice>,
    private val callback: Callback
) : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(context)
                .inflate(
                    R.layout.item_device,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(items[position]) {

            holder.tvName.text = name

            holder.tvAddress.text = address

            holder.tvOui.text = oui

            if (attached()) {
                holder.tvState.text = "Connected"
                holder.btnPair.visibility = View.INVISIBLE
                holder.btnUnpair.visibility = View.VISIBLE
            } else {
                holder.tvState.text = "New"
                holder.btnPair.visibility = View.VISIBLE
                holder.btnUnpair.visibility = View.INVISIBLE
            }
            initButtons(holder, position)
        }
    }

    private fun initButtons(holder: ViewHolder, position: Int) {
        holder.btnUnpair.setOnClickListener {
            callback.onDetachSelected(position)
        }
        holder.btnPair.setOnClickListener {
            callback.onAttachSelected(position)
        }
    }

    /**
     * Inner class which represents a single view in the RecyclerView
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.tv_device_name
        val tvAddress: TextView = itemView.tv_device_address
        val tvState: TextView = itemView.tv_device_state
        val btnPair: Button = itemView.btn_connect_device
        val btnUnpair: Button = itemView.btn_disconnect_device
        val tvOui : TextView = itemView.tv_device_oui
    }

    interface Callback {
        fun onAttachSelected(position: Int)
        fun onDetachSelected(position: Int)
    }

}