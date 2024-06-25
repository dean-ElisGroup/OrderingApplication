package com.elis.orderingapplication.sendOrder

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elis.orderingapplication.database.OrderInfoDao
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.pojo2.DeliveryAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SendDeliveryAddressViewModel(application: Application) : AndroidViewModel(application) {

    private val _navigateToOrderingGroup = MutableLiveData<String?>()
    val navigateToOrderingGroup
        get() = _navigateToOrderingGroup

    val database = OrderInfoDatabase.getInstance(application)
    val entities: LiveData<List<DeliveryAddress>> = database.orderInfoDao.getAll()


    fun onDeliveryAddressClicked(deliveryAddressNo: String?) {
        _navigateToOrderingGroup.value = deliveryAddressNo
    }
    fun onDeliveryAddressNavigated() {
        _navigateToOrderingGroup.value = null
    }
}