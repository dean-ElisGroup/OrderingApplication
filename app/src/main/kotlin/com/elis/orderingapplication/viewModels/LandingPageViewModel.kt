package com.elis.orderingapplication.viewModels

import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.database.OrderInfo
import com.elis.orderingapplication.database.OrderInfoDao
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.database.UserLoginDatabase
import kotlinx.coroutines.launch

class LandingPageViewModel (private val orderInfoDao: OrderInfoDao) : ViewModel() {

    fun insertOrder() {

        val order = OrderInfo(
            deliveryAddressName = "Dean",
            deliveryAddressNo = 665254
        )
        viewModelScope.launch {
            orderInfoDao.insert(order)
        }
    }
}