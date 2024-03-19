package com.elis.orderingapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.DeliveryAddressCardviewBinding
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.OrderInfo
import com.elis.orderingapplication.utils.ApiResponse

class DeliveryAddressAdapter :
    ListAdapter<DeliveryAddress, DeliveryAddressAdapter.DeliveryAddressViewHolder>(DiffCallback) {
    class DeliveryAddressViewHolder(private var binding: DeliveryAddressCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orderInfo: DeliveryAddress) {
            binding.deliveryAddress = orderInfo
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
            holder.bind(deliveryAddress)
        }
    }


/* private var binding: DeliveryAddressCardviewBindingImpl

 inner class ViewHolder(orderInfo: DeliveryAddressCardviewBindingImpl) : RecyclerView.ViewHolder(
     binding?.root!!
 ) {
     val textView: TextView
     init {
         textView = orderInfo.root.findViewById(R.id.textView)
     }
 }
 override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

     return ViewHolder(DataBindingUtil.inflate(
         LayoutInflater.from(parent.context),
         R.layout.delivery_address_cardview,
         parent, false))
 }

 override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     val testData = mList[position]
     holder.itemView.apply {
         FragmentDeliveryAddressBinding.bind(this).apply {
             holder.textView.text =
                 testData?.deliveryAddresses?.get(position)?.deliveryAddressName
         }
     }
 }

 override fun getItemCount(): Int {
     return mList.size
 }*/
