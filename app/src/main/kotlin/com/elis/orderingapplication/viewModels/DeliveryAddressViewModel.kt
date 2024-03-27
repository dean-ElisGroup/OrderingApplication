package com.elis.orderingapplication.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DeliveryAddressViewModel : ViewModel() {

    private val _navigateToOrderingGroup = MutableLiveData<String?>()
    val navigateToOrderingGroup
        get() = _navigateToOrderingGroup
    fun onDeliveryAddressClicked(deliveryAddressNo: String?) {
        _navigateToOrderingGroup.value = deliveryAddressNo
    }
    fun onDeliveryAddressNavigated() {
        _navigateToOrderingGroup.value = null
    }
}