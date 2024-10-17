package com.elis.orderingapplication.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.pojo2.JoinOrderingGroup


class OrderingGroupViewModel(
    application: Application,
    private val sharedViewModel: ParamsViewModel
) : AndroidViewModel(application) {

    private val _navigateToOrderingGroup = MutableLiveData<JoinOrderingGroup?>()
    val navigateToOrderingGroup: LiveData<JoinOrderingGroup?> = _navigateToOrderingGroup
    private val deliveryAddressNumber: LiveData<String> = sharedViewModel.getDeliveryAddressNumber()

    val database = OrderInfoDatabase.getInstance(application)
    val orderingGroup: LiveData<List<JoinOrderingGroup>> =
        database.orderInfoDao.getOrderingGroupList(deliveryAddressNumber.value.toString())

    fun onOrderingGroupClicked(orderingGroup: JoinOrderingGroup) {
        _navigateToOrderingGroup.value = orderingGroup
    }

    fun onOrderingGroupNavigated() {
        _navigateToOrderingGroup.value = null
    }

}