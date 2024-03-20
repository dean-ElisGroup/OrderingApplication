package com.elis.orderingapplication.adapters

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.GroupPosCardviewBinding
import com.elis.orderingapplication.pojo2.OrderingGroup

class OrderingGroupAdapter :
    ListAdapter<OrderingGroup, OrderingGroupAdapter.OrderingGroupViewHolder>(DiffCallback) {

    private lateinit var onClickListener: OnClickListener

    class OrderingGroupViewHolder(private var binding: GroupPosCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orderingGroup: OrderingGroup) {
            binding.orderingGroup = orderingGroup
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<OrderingGroup>() {
        override fun areItemsTheSame(oldItem: OrderingGroup, newItem: OrderingGroup): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: OrderingGroup,
            newItem: OrderingGroup
        ): Boolean {
            return oldItem.orderingGroupNo == newItem.orderingGroupNo
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderingGroupViewHolder {
        return OrderingGroupViewHolder(
            GroupPosCardviewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: OrderingGroupViewHolder, position: Int) {
        val orderingGroup = getItem(position)
        holder.bind(orderingGroup)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(position, getItem(position))
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int, orderingGroup: OrderingGroup)
    }
}