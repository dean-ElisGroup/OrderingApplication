package com.elis.orderingapplication.viewModels

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elis.orderingapplication.ArticleEntryCardFragment
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.OrderInfo
import com.elis.orderingapplication.pojo2.OrderRowsItem
import com.elis.orderingapplication.utils.ApiResponse

class ParamsViewModel : ViewModel() {
    private val _orderDate = MutableLiveData<String>("")
    val orderDate: LiveData<String> = _orderDate

    private lateinit var _sessionKey: String

    private val _posTotal = MutableLiveData<Int>()
    val posTotal: LiveData<Int> = _posTotal

    private val _articleTotal = MutableLiveData<Int?>()
    val articleTotal: LiveData<Int?> = _articleTotal

    private val _appVersion = MutableLiveData<String>("")
    val appVersion: LiveData<String> = _appVersion

    private val _flavor = MutableLiveData<String>("")
    val flavor: LiveData<String> = _flavor

    private var _orderInfo: ApiResponse<OrderInfo>? = null
    val orderInfo: ApiResponse<OrderInfo>? = _orderInfo

    private var _deliveryAddress: List<DeliveryAddress>? = null


    private var _orders: Order? = null
    val orders: Order? = _orders

    private val _orderRowsItem = MutableLiveData<List<OrderRowsItem?>?>()
    val orderRowsItem: MutableLiveData<List<OrderRowsItem?>?> = _orderRowsItem

    private var _deliveryAddressName = MutableLiveData<String>("")
    val deliveryAddressName: LiveData<String> = _deliveryAddressName

    private var _deliveryAddressNum = MutableLiveData<String>("")
    private val deliveryAddressNum: LiveData<String> = _deliveryAddressNum

    private var _orderingGroupName = MutableLiveData<String?>("")
    val orderingGroupName: MutableLiveData<String?> = _orderingGroupName

    private var _orderingGroupNo = MutableLiveData<String>("")
    val orderingGroupNo: LiveData<String> = _orderingGroupNo

    private var _pointOfServiceNo = MutableLiveData<String>("")
    private val posNo: LiveData<String> = _pointOfServiceNo

    private var _appOrderId = MutableLiveData<String>("")
    private val appOrderId: LiveData<String> = _appOrderId

    private var _articleDeliveryDate = MutableLiveData<String>("")
    private val articleDeliveryDate: LiveData<String> = _articleDeliveryDate

    val argsBundleFromTest = MutableLiveData<Bundle>()

    private var lastArticleCallback: ArticleEntryCardFragment.LastArticleCallback? = null
    private var orderStatusCallback: ArticleEntryCardFragment.OrderStatusCallback? = null

    fun setOrderDate(orderDate: String) {
        _orderDate.value = orderDate
    }

    fun setSessionKey(sessionKey: String) {
        _sessionKey = sessionKey
    }

    fun getSessionKey(): String {
        return _sessionKey
    }

    fun setDeliveryAddressNum(data: String) {
        _deliveryAddressNum.value = data
    }

    fun getDeliveryAddressNumber(): LiveData<String> {
        return deliveryAddressNum
    }

    fun setArticleAppOrderId(data: String) {
        _appOrderId.value = data
    }

    fun getArticleAppOrderId(): LiveData<String> {
        return appOrderId
    }

    fun setArticleDeliveryDate(data: String) {
        _articleDeliveryDate.value = data
    }

    fun getArticleDeliveryDate(): LiveData<String> {
        return articleDeliveryDate
    }

    fun setPosNum(data: String) {
        _pointOfServiceNo.value = data
    }

    fun getPosNum(): LiveData<String> {
        return posNo
    }

    fun setArticleTotal(articleTotal: Int?) {
        _articleTotal.value = articleTotal
    }

    fun setOrderInfo(orderInfo: ApiResponse<OrderInfo>?) {
        _orderInfo = orderInfo
    }

    fun getOrder(): ApiResponse<OrderInfo>? {
        return _orderInfo
    }

    fun setDeliveryAddress(deliveryAddress: ApiResponse<OrderInfo>?) {
        _deliveryAddress = deliveryAddress?.data?.deliveryAddresses
    }

    fun setDeliveryAddressName(deliveryAddressName: String) {
        _deliveryAddressName.value = deliveryAddressName
    }

    fun setOrderingGroupName(orderingGroupName: String?) {
        _orderingGroupName.value = orderingGroupName
    }

    fun setOrderingGroupNo(data: String) {
        _orderingGroupNo.value = data
    }

    fun getOrderingGroupNum(): LiveData<String> {
        return orderingGroupNo
    }

    fun setAppVersion(appVersion: String) {
        val version = "Version: "
        _appVersion.value = version.plus("").plus(appVersion)
    }

    fun setFlavor(flavor: String) {
        _flavor.value = flavor
    }

    fun setLastArticleCallback(callback: ArticleEntryCardFragment.LastArticleCallback) {
        lastArticleCallback = callback
    }

    fun getLastArticleCallback(): ArticleEntryCardFragment.LastArticleCallback? {
        return lastArticleCallback
    }

    fun setOrderStatusCallback(callback: ArticleEntryCardFragment.OrderStatusCallback) {
        orderStatusCallback = callback
    }

    fun getOrderStatusCallback(): ArticleEntryCardFragment.OrderStatusCallback? {
        return orderStatusCallback
    }
}