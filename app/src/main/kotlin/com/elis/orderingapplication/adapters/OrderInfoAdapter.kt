package com.elis.orderingapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.R
import com.elis.orderingapplication.databinding.FragmentLandingPageBinding
import com.elis.orderingapplication.model.OrderingInfoResponse
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.OrderInfo

class OrderInfoAdapter(private val mList: ArrayList<OrderInfo?>) : RecyclerView.Adapter<OrderInfoAdapter.OrderInfoHolder>() {
    inner class OrderInfoHolder(orderInfo: View) : RecyclerView.ViewHolder(orderInfo){
        val textView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderInfoHolder {
        return OrderInfoHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.delivery_address_cardview, parent, false)
        )

    }

    override fun onBindViewHolder(holder: OrderInfoHolder, position: Int) {
        val testData = mList[position]
        holder.itemView.apply {
            FragmentLandingPageBinding.bind(this).apply {
                holder.textView.text = testData?.deliveryAddresses?.get(position)?.deliveryAddressName
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}
