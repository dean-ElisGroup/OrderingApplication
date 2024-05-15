package com.elis.orderingapplication.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.OrderInfo
import com.elis.orderingapplication.pojo2.OrderingGroup
import com.elis.orderingapplication.pojo2.PointsOfService

class BindingAdapters {
    @BindingAdapter("deliveryAddressListData")
    fun bindRecyclerView(recyclerView: RecyclerView, data: List<DeliveryAddress>?) {
        val adapter = recyclerView.adapter as DeliveryAddressAdapter
        adapter.submitList(data)
    }

    @BindingAdapter("orderingGroupsListData")
    fun bindRecyclerViewOrderingGroups(recyclerView: RecyclerView, data: List<OrderingGroup>?) {
        val adapter = recyclerView.adapter as OrderingGroupAdapter
        adapter.submitList(data)
    }

    @BindingAdapter("orderListData")
    fun bindRecyclerViewOrder(recyclerView: RecyclerView, data: List<Order>?) {
        val adapter = recyclerView.adapter as OrderAdapter
        adapter.submitList(data)
    }
companion object {
    @BindingAdapter("conditionalMargin")
    @JvmStatic
    fun setConditionalMargin(textView: TextView, condition: Boolean) {
        val layoutParams = textView.layoutParams as ViewGroup.MarginLayoutParams
        val margin =
        if (condition) {
            layoutParams.setMargins(8, 3, 8, 0)
        } else {
            layoutParams.setMargins(8, 43, 8, 0)
        }
        textView.layoutParams = layoutParams
    }
}
}

