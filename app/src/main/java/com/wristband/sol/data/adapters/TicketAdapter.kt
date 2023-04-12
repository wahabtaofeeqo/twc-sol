package com.wristband.sol.data.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wristband.sol.data.model.AttendanceWithMember
import com.wristband.sol.data.model.Ticket
import com.wristband.sol.databinding.AttendanceListItemBinding
import com.wristband.sol.databinding.TicketListItemBinding
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TicketAdapter @Inject constructor(): PagingDataAdapter<Ticket, TicketAdapter.TicketViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<Ticket>() {
            override fun areItemsTheSame(oldModel: Ticket, newModel: Ticket) = oldModel.tid == newModel.tid

            override fun areContentsTheSame(oldModel: Ticket, newModel: Ticket) = oldModel == newModel
        }
    }

    class TicketViewHolder(private val binding: TicketListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindTo(model: Ticket) {
            if(absoluteAdapterPosition % 2 != 0) {
                binding.root.setBackgroundColor(Color.LTGRAY)
            }

            binding.id.text = "${absoluteAdapterPosition + 1}"
            binding.name.text = model.name
            binding.access.text = model.accessType
            binding.cost.text = model.cost.toString()
//            val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(model.date)
//            binding.expired.text = date
        }
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bindTo(getItem(position)!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TicketListItemBinding.inflate(inflater, parent, false)
        return TicketViewHolder(binding)
    }
}