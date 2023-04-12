package com.wristband.sol.data.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wristband.sol.data.model.AttendanceWithMember
import com.wristband.sol.databinding.AttendanceListItemBinding
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AttendanceAdapter @Inject constructor(): PagingDataAdapter<AttendanceWithMember, AttendanceAdapter.AttendanceViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<AttendanceWithMember>() {
            override fun areItemsTheSame(oldModel: AttendanceWithMember, newModel: AttendanceWithMember) = oldModel.aid == newModel.aid

            override fun areContentsTheSame(oldModel: AttendanceWithMember, newModel: AttendanceWithMember) = oldModel == newModel
        }
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder.bindTo(getItem(position)!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AttendanceListItemBinding.inflate(inflater, parent, false)
        return AttendanceViewHolder(binding)
    }

    class AttendanceViewHolder(private val binding: AttendanceListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindTo(model: AttendanceWithMember) {
            if(absoluteAdapterPosition % 2 != 0) {
                binding.root.setBackgroundColor(Color.LTGRAY)
            }

            binding.id.text = "${absoluteAdapterPosition + 1}"
            binding.name.text = model.name
            binding.category.text = model.category
            binding.code.text = model.code
            binding.email.text = model.email
            binding.phone.text = model.phone
            val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(model.date)
            binding.expired.text = date
        }
    }
}