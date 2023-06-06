package com.wristband.sol.data.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.wristband.sol.data.model.BluetoothDetails

import com.wristband.sol.ui.home.placeholder.PlaceholderContent.PlaceholderItem
import com.wristband.sol.databinding.FragmentBluetoothListBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class BluetoothDeviceAdapter: RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder>() {

    lateinit var listener: ItemClickListener
    private val items = ArrayList<BluetoothDetails>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentBluetoothListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.contentView.text = item.name
        //holder.contentView.setOnClickListener { listener.onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(binding: FragmentBluetoothListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val contentView: TextView = binding.content.apply {
            setOnClickListener {listener.onItemClick(items[absoluteAdapterPosition])}
        }
    }

    fun addItem(details: BluetoothDetails) {
        var found = false
        for (item in items) {
            if (item.mac == details.mac) {
                found = true
                break
            }
        }

        if(!found) {
            items.add(details)
            notifyItemInserted(items.size)
        }
    }

    interface ItemClickListener {
        fun onItemClick(details: BluetoothDetails)
    }
}