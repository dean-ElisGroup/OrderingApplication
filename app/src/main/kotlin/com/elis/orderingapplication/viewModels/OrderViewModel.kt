package com.elis.orderingapplication.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elis.orderingapplication.pojo2.Order

class OrderViewModel: ViewModel(){

    private val _navigateToOrder = MutableLiveData<Order?>()
    val navigateToOrder
        get() = _navigateToOrder
    fun onOrderClicked(order: Order?) {
        _navigateToOrder.value = order
    }
    fun onOrderNavigated() {
        _navigateToOrder.value = null
    }

}