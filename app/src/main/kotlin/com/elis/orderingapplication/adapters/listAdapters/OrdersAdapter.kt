package com.elis.orderingapplication.adapters.listAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.OrderCardviewBinding
import com.elis.orderingapplication.pojo2.Order

class OrdersAdapter(private val clickListener: OrdersAdapter.MyClickListener) :
    RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    private var data: List<Order> = emptyList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrdersViewHolder {
        return OrdersViewHolder(
            OrderCardviewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val orders = data[position]
        holder.bind(clickListener, orders)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(newData: List<Order>) {
        data = newData
        notifyDataSetChanged()
    }

    interface MyClickListener {
        fun onItemClick(myData: Order)
    }

    class OrdersViewHolder(private var binding: OrderCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: MyClickListener, orders: Order) {
            binding.order = orders
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }
}