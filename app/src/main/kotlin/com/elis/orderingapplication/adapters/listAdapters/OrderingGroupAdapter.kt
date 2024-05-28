package com.elis.orderingapplication.adapters.listAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.OrderingGroupCardviewBinding
import com.elis.orderingapplication.pojo2.JoinOrderingGroup
import com.elis.orderingapplication.pojo2.OrderingGroup

class OrderingGroupAdapter(private val clickListener: MyClickListener) :
    RecyclerView.Adapter<OrderingGroupAdapter.OrderingGroupViewHolder>() {

    private var data: List<JoinOrderingGroup> = emptyList()
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
        val orderingGroup = data[position]
        holder.bind(clickListener, orderingGroup)
    }
    override fun getItemCount(): Int {
        return data.size
    }
    fun setData(newData: List<JoinOrderingGroup>) {
        data = newData
        notifyDataSetChanged()
    }
    interface MyClickListener {
        fun onItemClick(myData: JoinOrderingGroup)
    }
    class OrderingGroupViewHolder(private var binding: OrderingGroupCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: MyClickListener, orderingGroup: JoinOrderingGroup) {
            binding.orderingGroup = orderingGroup
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }
}