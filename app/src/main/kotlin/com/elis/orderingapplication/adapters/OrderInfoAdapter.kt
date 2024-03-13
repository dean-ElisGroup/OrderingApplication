package com.elis.orderingapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.R
import com.elis.orderingapplication.databinding.FragmentLandingPageBinding
import com.elis.orderingapplication.model.OrderingInfoResponse
import com.elis.orderingapplication.model.OrderingOrderInfoResponseStruct


class OrderInfoAdapter : RecyclerView.Adapter<OrderInfoAdapter.OrderInfoHolder>() {

    inner class OrderInfoHolder(orderInfo: View) : RecyclerView.ViewHolder(orderInfo)

    private val differCallBack = object : DiffUtil.ItemCallback<OrderingInfoResponse>() {
        override fun areItemsTheSame(
            oldItem: OrderingInfoResponse,
            newItem: OrderingInfoResponse
        ): Boolean {
            return oldItem.orderingOrderInfoResponseStruct == newItem.orderingOrderInfoResponseStruct
        }

        override fun areContentsTheSame(
            oldItem: OrderingInfoResponse,
            newItem: OrderingInfoResponse
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderInfoHolder {
        return OrderInfoHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_landing_page, parent, false)
        )

    }

    override fun onBindViewHolder(holder: OrderInfoHolder, position: Int) {
        val loginSession = differ.currentList[position]
        holder.itemView.apply {
            FragmentLandingPageBinding.bind(this).apply {
                sessionKey.text = loginSession.orderingOrderInfoResponseStruct.deliveryAddresses.orderingDeliveryAddressStruct.deliveryAddressName
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}