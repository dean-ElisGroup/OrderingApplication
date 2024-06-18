package com.elis.orderingapplication.adapters

import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseMethod
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.OrderingGroup
import org.jetbrains.annotations.NotNull
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

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

    @BindingAdapter("dateFormat")
    @JvmStatic
    fun dateFormat(textView: TextView, date: String): String {

        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.getDefault())
        val date = LocalDate.parse(date, inputFormatter)

        val finalDate = outputFormatter.format(date)

        return finalDate
    }
}
}