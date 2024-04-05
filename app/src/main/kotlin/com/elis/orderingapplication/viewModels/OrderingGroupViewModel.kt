package com.elis.orderingapplication.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderingGroupViewModel: ViewModel(){

    private val _navigateToPosGroup = MutableLiveData<String?>()
    val navigateToOrderingGroup
        get() = _navigateToPosGroup
    fun onOrderingGroupClicked(orderingGroup: String?) {
        _navigateToPosGroup.value = orderingGroup
    }
    fun onOrderingGroupNavigated() {
        _navigateToPosGroup.value = null
    }


}