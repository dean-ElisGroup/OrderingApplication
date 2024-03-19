package com.elis.orderingapplication.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.OrderInfo
import com.elis.orderingapplication.utils.ApiResponse

class ParamsViewModel : ViewModel() {
    private val _orderDate = MutableLiveData<String>("")
    val orderDate: LiveData<String> = _orderDate

    private lateinit var _sessionKey: String

    private val _appVersion = MutableLiveData<String>("")
    val appVersion: LiveData<String> = _appVersion

    private val _flavor = MutableLiveData<String>("")
    val flavor: LiveData<String> = _flavor

    private var _orderInfo: ApiResponse<OrderInfo>? = null
    val orderInfo: ApiResponse<OrderInfo>? = _orderInfo

    private var _deliveryAddress: List<DeliveryAddress>? = null
    val deliveryAddress: List<DeliveryAddress>? = _deliveryAddress


    fun setOrderDate(orderDate: String) {
        _orderDate.value = orderDate
    }

    fun setSessionKey(sessionKey: String) {
        _sessionKey = sessionKey
    }

    fun getSessionKey(): String {
        return _sessionKey
    }
    fun setOrderInfo(orderInfo: ApiResponse<OrderInfo>?){
        _orderInfo = orderInfo
    }
    fun getOrder(): ApiResponse<OrderInfo>? {
        return _orderInfo
   }
    fun setDeliveryAddress(deliveryAddress: ApiResponse<OrderInfo>?) {
        _deliveryAddress = deliveryAddress?.data?.deliveryAddresses
    }
    fun getDeliveryAddresses(): List<DeliveryAddress>? {
        return _deliveryAddress
    }

    fun setAppVersion(appVersion: String) {
        val version = "Version: "
        _appVersion.value = version.plus("").plus(appVersion)
    }

    fun setFlavor(flavor: String) {
        _flavor.value = flavor
    }

    fun hasNoOrderDate(): Boolean {
        return _orderDate.value.isNullOrEmpty()
    }

    fun hasNoAppVersion(): Boolean {
        return _appVersion.value.isNullOrEmpty()
    }


}