package com.elis.orderingapplication.adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.OrderInfo
class BindingAdapters {
    @BindingAdapter("deliveryAddressListData")
    fun bindRecyclerView(recyclerView: RecyclerView, data: List<DeliveryAddress>?) {
        val adapter = recyclerView.adapter as DeliveryAddressAdapter
        adapter.submitList(data)
    }
}
