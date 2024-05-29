package com.elis.orderingapplication.adapters.listAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.DeliveryAddressCardviewBinding
import com.elis.orderingapplication.pojo2.DeliveryAddress

class DeliveryAdapter(private val clickListener: MyClickListener) :
    RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {

    private var data: List<DeliveryAddress> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeliveryViewHolder {
        return DeliveryViewHolder(
            DeliveryAddressCardviewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        val deliveryAddress = data[position]
        holder.bind(clickListener, deliveryAddress)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(newData: List<DeliveryAddress>) {
        data = newData
        notifyDataSetChanged()
    }
    //interface MyClickListener{
    //    fun onItemClick(myData: DeliveryAddress) = MyClickListener(myData.deliveryAddressNo)
    //}

    interface MyClickListener {
        fun onItemClick(myData: DeliveryAddress)
    }

    class DeliveryViewHolder(private var binding: DeliveryAddressCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: MyClickListener, orderInfo: DeliveryAddress) {
            binding.deliveryAddressNo.text = orderInfo.deliveryAddressNo
            binding.deliveryAddressName.text = orderInfo.deliveryAddressName
            binding.deliveryAddress = orderInfo
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }
}