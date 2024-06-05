package com.elis.orderingapplication.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.elis.orderingapplication.constants.Constants
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.pojo2.Order
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrderViewModel(application: Application, private val sharedViewModel: ParamsViewModel) : AndroidViewModel(application) {

    private val _navigateToOrder = MutableLiveData<Order?>()

    val database = OrderInfoDatabase.getInstance(application)
    val orders: LiveData<List<Order>> = database.orderInfoDao.getOrders(getDeliveryAddressNum().value.toString(),getPointOfServiceNum().value.toString(),getOrderDate(), Constants.ORDER_STATUS_ORDER_TOO_LATE)

    private fun getOrderDate(): String {
        var orderDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDateTime.now().format(orderDateFormatter)
    }
    private fun getDeliveryAddressNum(): LiveData<String> {
        return sharedViewModel.getDeliveryAddressNumber()
    }
    private fun getPointOfServiceNum(): LiveData<String> {
        return sharedViewModel.getPosNum()
    }

    val navigateToOrder
        get() = _navigateToOrder
    fun onOrderClicked(order: Order?) {
        _navigateToOrder.value = order
    }
    fun onOrderNavigated() {
        _navigateToOrder.value = null
    }

}