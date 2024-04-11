package com.elis.orderingapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.OrderCardviewBinding
import com.elis.orderingapplication.databinding.PosCardviewBinding
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.PointsOfService
import com.elis.orderingapplication.viewModels.ParamsViewModel

class OrderAdapter(private val clickListener: OrderListener) :
    ListAdapter<Order, OrderAdapter.OrderViewHolder>(DiffCallback) {


    class OrderViewHolder(private var binding: OrderCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(clickListener: OrderListener, order: Order) {
            binding.order = order
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }
    companion object DiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: Order,
            newItem: Order
        ): Boolean {
            return oldItem.orderDate == newItem.orderDate
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {
        return OrderViewHolder(
            OrderCardviewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(clickListener, order)
    }

    class OrderListener(val clickListener: (order: Order?) -> Unit) {
        fun onClick(order: Order) =
            clickListener(order)
    }


}



