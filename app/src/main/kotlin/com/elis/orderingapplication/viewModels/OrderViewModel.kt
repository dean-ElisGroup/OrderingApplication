package com.elis.orderingapplication.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.constants.Constants
import com.elis.orderingapplication.constants.Constants.Companion.APP_STATUS_STARTED
import com.elis.orderingapplication.constants.Constants.Companion.ORDER_STATUS_FINISHED
import com.elis.orderingapplication.constants.Constants.Companion.ORDER_STATUS_NEW
import com.elis.orderingapplication.constants.Constants.Companion.ORDER_STATUS_STARTED
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.pojo2.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrderViewModel(application: Application, private val sharedViewModel: ParamsViewModel) :
    AndroidViewModel(application) {

    private val _navigateToOrder = MutableLiveData<Order?>()

    var deliveryAddressName: String? = null
    var pointOfServiceName: String? = null

    val database = OrderInfoDatabase.getInstance(application)
    val orders: LiveData<List<Order>> = database.orderInfoDao.getOrders(
        getDeliveryAddressNum().value.toString(),
        getPointOfServiceNum().value.toString(),
        getOrderDate(),
        listOf(ORDER_STATUS_NEW, ORDER_STATUS_STARTED, ORDER_STATUS_FINISHED)
    )

    private fun getOrderDate(): String {
        val orderDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val orderDate = LocalDateTime.now().format(orderDateFormatter)
        return orderDate

    }

    private fun getDeliveryAddressNum(): LiveData<String> {
        return sharedViewModel.getDeliveryAddressNumber()
    }

    private fun getPointOfServiceNum(): LiveData<String> {
        return sharedViewModel.getPosNum()
    }

    fun updateOrderStatus(order: Order) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                withContext(Dispatchers.IO) {
                    database.orderInfoDao.updateOrderStatus(
                        order.appOrderId,
                        APP_STATUS_STARTED.toString(),
                        ORDER_STATUS_STARTED
                    )
                }
            }
        }
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