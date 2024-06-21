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
    private val _navigateToPosGroup = MutableLiveData<JoinOrderingGroup?>()

    val navigateToOrderingGroup
        get() = _navigateToPosGroup

    val database = OrderInfoDatabase.getInstance(application)
    val orderingGroup: LiveData<List<JoinOrderingGroup>> =
        database.orderInfoDao.getOrderingGroupList(getDeliveryAddressNum().value.toString())

    private fun getDeliveryAddressNum(): LiveData<String> {
        return sharedViewModel.getDeliveryAddressNumber()
    }

    fun onOrderingGroupClicked(orderingGroup: JoinOrderingGroup) {
        _navigateToPosGroup.value = orderingGroup
    }

    fun onOrderingGroupNavigated() {
        _navigateToPosGroup.value = null
    }


}