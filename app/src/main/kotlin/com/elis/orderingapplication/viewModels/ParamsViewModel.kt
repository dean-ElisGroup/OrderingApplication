package com.elis.orderingapplication.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elis.orderingapplication.model.OrderingRequest

class ParamsViewModel : ViewModel() {
    private val _orderDate = MutableLiveData<String>("")
    val orderDate: LiveData<String> = _orderDate

    //private val _sessionKey = MutableLiveData<String>("")
    //val sessionKey: LiveData<String> = _sessionKey

    private lateinit var _sessionKey: String

    private val _appVersion = MutableLiveData<String>("")
    val appVersion: LiveData<String> = _appVersion

    private val _flavor = MutableLiveData<String>("")
    val flavor: LiveData<String> = _flavor

    fun setOrderDate(orderDate: String) {
        _orderDate.value = orderDate
    }

    fun setSessionKey(sessionKey: String) {
        _sessionKey = sessionKey
    }

    fun getSessionKey(): String {
        return _sessionKey
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

    //fun hasNoSessionKey(): Boolean {
    //    return _sessionKey.value.isNullOrEmpty()
   // }

    fun hasNoAppVersion(): Boolean {
        return _appVersion.value.isNullOrEmpty()
    }
}