package com.elis.orderingapplication.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elis.orderingapplication.pojo2.OrderingGroup

class OrderingGroupViewModel: ViewModel(){

    //private val _navigateToPosGroup = MutableLiveData<String?>()
    private val _navigateToPosGroup = MutableLiveData<OrderingGroup?>()

    val navigateToOrderingGroup
        get() = _navigateToPosGroup
    //fun onOrderingGroupClicked(orderingGroup: String?) {
      //  _navigateToPosGroup.value = orderingGroup
    //}
    fun onOrderingGroupClicked(orderingGroup: OrderingGroup) {
        _navigateToPosGroup.value = orderingGroup
    }

    fun onOrderingGroupNavigated() {
        _navigateToPosGroup.value = null
    }


}