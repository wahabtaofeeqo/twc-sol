package com.wristband.sol.data.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wristband.sol.data.model.Member
import com.wristband.sol.databinding.MemberListItemBinding
import javax.inject.Inject

class MemberAdapter @Inject constructor(): PagingDataAdapter<Member, MemberAdapter.MemberViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<Member>() {
            override fun areItemsTheSame(oldMember: Member, newMember: Member) = oldMember.id == newMember.id

            override fun areContentsTheSame(oldMember: Member, newMember: Member) = oldMember == newMember
        }
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = getItem(position)!!
        holder.bindTo(member)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MemberListItemBinding.inflate(inflater, parent, false)
        return MemberViewHolder(binding)
    }


    class MemberViewHolder(private val binding: MemberListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindTo(member: Member) {
            if(absoluteAdapterPosition % 2 != 0) {
                binding.root.setBackgroundColor(Color.LTGRAY)
            }

            binding.name.text = member.name
            binding.code.text = member.code
            binding.phone.text = member.phone
            binding.email.text = member.email
            binding.category.text = member.category
        }
    }
}