package com.elis.orderingapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.DeliveryAddressCardviewBinding
import com.elis.orderingapplication.pojo2.DeliveryAddress

class DeliveryAddressAdapter(private val clickListener: DeliveryAddressListener) :
    ListAdapter<DeliveryAddress, DeliveryAddressAdapter.DeliveryAddressViewHolder>(DiffCallback) {

    class DeliveryAddressViewHolder(private var binding: DeliveryAddressCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: DeliveryAddressListener,orderInfo: DeliveryAddress) {
            binding.deliveryAddress = orderInfo
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DeliveryAddress>() {
        override fun areItemsTheSame(oldItem: DeliveryAddress, newItem: DeliveryAddress): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: DeliveryAddress,
            newItem: DeliveryAddress
        ): Boolean {
            return oldItem.deliveryAddressNo == newItem.deliveryAddressNo
        }
    }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): DeliveryAddressViewHolder {
            return DeliveryAddressViewHolder(
                DeliveryAddressCardviewBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    )
                )
            )
        }

        override fun onBindViewHolder(holder: DeliveryAddressViewHolder, position: Int) {
            val deliveryAddress = getItem(position)
            //holder.bind(deliveryAddress)
            holder.bind(clickListener,deliveryAddress)
        }

    class DeliveryAddressListener(val clickListener: (deliveryAddressNo: String?, deliveryAddressName: String?) -> Unit) {
        fun onClick(deliveryAddress: DeliveryAddress) = clickListener(deliveryAddress.deliveryAddressNo, deliveryAddress.deliveryAddressName)
    }




}



