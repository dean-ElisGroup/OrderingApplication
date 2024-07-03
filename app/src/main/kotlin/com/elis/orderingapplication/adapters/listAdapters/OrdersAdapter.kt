package com.elis.orderingapplication.adapters.listAdapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.R
import com.elis.orderingapplication.constants.Constants
import com.elis.orderingapplication.databinding.OrderCardviewBinding
import com.elis.orderingapplication.pojo2.Order
import com.google.android.material.card.MaterialCardView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.mainCardView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(newData: List<Order>) {
        data = newData
        notifyDataSetChanged()
    }

    interface MyClickListener {
        fun onItemClick(myData: Order, isClickable: Boolean, orderStatus: String?)
    }

    class OrdersViewHolder(private var binding: OrderCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: MyClickListener, orders: Order) {
            val orderStatus = orders.appOrderStatus
            binding.order = orders
            binding.deliveryDate = getOrderDate(orders.deliveryDate)
            binding.clickListener = clickListener
            orderStatus?.let { getImageResourceForOrderStatus(it) }
                ?.let { binding.newStatus.setImageResource(it) }
            binding.statusIconColour = getIconColourForStatus(orderStatus, binding.root.context)
            binding.buttonColour =
                getColourResourceForOrderStatus(orderStatus, binding.root.context)
            binding.buttonTextColor = getTextColorForStatus(orderStatus, binding.root.context)
            binding.warningText = setWarningText(orderStatus)
            // Controls if the Order button is clickable depending on the order status
            binding.buttonLayout.isClickable = setButtonClickable(orderStatus)

            binding.buttonLayout.setOnClickListener {
                clickListener.onItemClick(orders, setButtonClickable(orderStatus), orderStatus)
            }

            binding.executePendingBindings()
        }

        fun getOrderDate(orderDate: String?): String {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val outputFormatter =
                DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.getDefault())
            val date = LocalDate.parse(orderDate, inputFormatter)

            return outputFormatter.format(date)
        }

        private fun getImageResourceForOrderStatus(orderStatus: String): Int {
            return when (orderStatus) {
                Constants.APP_STATUS_NEW.toString() -> R.drawable.baseline_fiber_new_24
                Constants.APP_STATUS_STARTED.toString() -> R.drawable.baseline_edit_24
                Constants.APP_STATUS_FINISHED.toString() -> R.drawable.baseline_cancel_schedule_send_24
                Constants.APP_STATUS_SENT.toString() -> R.drawable.baseline_send_24
                else -> R.drawable.baseline_question_mark_24
            }
        }

        private fun getColourResourceForOrderStatus(orderStatus: String?, context: Context): Int {
            return when (orderStatus) {
                Constants.APP_STATUS_NEW.toString() -> ContextCompat.getColor(
                    context,
                    R.color.white
                )

                Constants.APP_STATUS_STARTED.toString() -> ContextCompat.getColor(
                    context,
                    R.color.elis_green
                )

                Constants.APP_STATUS_FINISHED.toString() -> ContextCompat.getColor(
                    context,
                    R.color.elis_red
                )

                Constants.APP_STATUS_SENT.toString() -> ContextCompat.getColor(
                    context,
                    R.color.elis_send_green
                )

                else -> ContextCompat.getColor(context, R.color.white)
            }
        }

        private fun getTextColorForStatus(orderStatus: String?, context: Context): Int {
            return when (orderStatus) {
                Constants.APP_STATUS_NEW.toString() -> ContextCompat.getColor(
                    context,
                    R.color.elis_black
                )

                Constants.APP_STATUS_STARTED.toString() -> ContextCompat.getColor(
                    context,
                    R.color.white
                )

                Constants.APP_STATUS_FINISHED.toString() -> ContextCompat.getColor(
                    context,
                    R.color.white
                )

                Constants.APP_STATUS_SENT.toString() -> ContextCompat.getColor(
                    context,
                    R.color.white
                )

                else -> ContextCompat.getColor(context, R.color.elis_black)
            }
        }

        private fun getIconColourForStatus(orderStatus: String?, context: Context): Int {
            return when (orderStatus) {
                Constants.APP_STATUS_NEW.toString() -> ContextCompat.getColor(
                    context,
                    R.color.elis_black
                )

                Constants.APP_STATUS_STARTED.toString() -> ContextCompat.getColor(
                    context,
                    R.color.white
                )

                Constants.APP_STATUS_FINISHED.toString() -> ContextCompat.getColor(
                    context,
                    R.color.white
                )

                Constants.APP_STATUS_SENT.toString() -> ContextCompat.getColor(
                    context,
                    R.color.white
                )

                else -> ContextCompat.getColor(context, R.color.elis_black)
            }
        }

        private fun setWarningText(orderStatus: String?): String {
            return when (orderStatus) {
                Constants.APP_STATUS_FINISHED.toString() -> "Order has been finished but not submitted."
                else -> ""
            }
        }

        private fun setButtonClickable(orderStatus: String?): Boolean {
            return when (orderStatus) {
                Constants.APP_STATUS_FINISHED.toString() -> false
                Constants.APP_STATUS_SENT.toString() -> false
                else -> true
            }
        }
    }
}