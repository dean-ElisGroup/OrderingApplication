package com.elis.orderingapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.OrderingGroupCardviewBinding
import com.elis.orderingapplication.pojo2.OrderingGroup
import com.elis.orderingapplication.pojo2.PointsOfService
import com.elis.orderingapplication.viewModels.ParamsViewModel

class OrderingGroupAdapter(private val clickListener: OrderingGroupListener) :
    ListAdapter<OrderingGroup, OrderingGroupAdapter.OrderingGroupViewHolder>(DiffCallback){

    class OrderingGroupViewHolder(private var binding: OrderingGroupCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: OrderingGroupListener, orderingGroup: OrderingGroup) {
            binding.orderingGroup = orderingGroup
            binding.clickListener = clickListener
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
            OrderingGroupCardviewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: OrderingGroupViewHolder, position: Int) {
        val orderingGroup = getItem(position)
        holder.bind(clickListener, orderingGroup)
    }

    //class OrderingGroupListener(val clickListener: (orderingGroupDescription: String?) -> Unit) {
    //    fun onClick(orderingGroup: OrderingGroup) =
    //        clickListener(orderingGroup.orderingGroupDescription)
    //}
    class OrderingGroupListener(val clickListener: (orderingGroupDescription: OrderingGroup) -> Unit) {
        fun onClick(orderingGroup: OrderingGroup) =
            clickListener(orderingGroup)
    }

}



