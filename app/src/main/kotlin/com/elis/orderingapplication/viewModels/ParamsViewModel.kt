package com.elis.orderingapplication.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.OrderInfo
import com.elis.orderingapplication.pojo2.OrderRowsItem
import com.elis.orderingapplication.pojo2.OrderingGroup
import com.elis.orderingapplication.pojo2.PointsOfService
import com.elis.orderingapplication.utils.ApiResponse

class ParamsViewModel : ViewModel() {
    private val _orderDate = MutableLiveData<String>("")
    val orderDate: LiveData<String> = _orderDate

    private lateinit var _sessionKey: String

    private var _deliveryAddressNo: String = ""
    private var _pointOfServiceNo: String = ""

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

    private var _orderingGroups: List<OrderingGroup>? = null

    private var _pointsOfService: List<PointsOfService>? = null

    private var _filteredPointsOfService: List<PointsOfService>? = null

    private var _filteredOrders: List<Order>? = null

    private var _orders: Order? = null
    val orders: Order? = _orders

    private val _orderRowsItem = MutableLiveData<List<OrderRowsItem?>?>()
    val orderRowsItem: MutableLiveData<List<OrderRowsItem?>?> = _orderRowsItem

    //private var _orders = MutableLiveData<Order>(null)
    //val order: LiveData<Order> = _orders


    private var _deliveryAddressName = MutableLiveData<String>("")
    val deliveryAddressName: LiveData<String> = _deliveryAddressName

    private var _orderingGroupName = MutableLiveData<String?>("")
    val orderingGroupName: MutableLiveData<String?> = _orderingGroupName

    private var _orderingGroupNo = MutableLiveData<String?>("")
    val orderingGroupNo: MutableLiveData<String?> = _orderingGroupNo

    fun setOrderDate(orderDate: String) {
        _orderDate.value = orderDate
    }

    fun setSessionKey(sessionKey: String) {
        _sessionKey = sessionKey
    }

    fun getSessionKey(): String {
        return _sessionKey
    }

    fun setDeliveryAddressNo(deliveryAddressNo: String) {
        _deliveryAddressNo = deliveryAddressNo
    }

    fun getDeliveryAddressNo(): String {
        return _deliveryAddressNo
    }

    fun setPointOfServiceNo(pointOfServiceNo: String) {
        _pointOfServiceNo = pointOfServiceNo
    }

    fun getPointOfServiceNo(): String {
        return _pointOfServiceNo
    }

    fun setPOSTotal(posTotal: Int) {
        _posTotal.value = posTotal
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

    fun setPointsOfService(pos: List<PointsOfService>?) {
        _pointsOfService = pos
    }

    fun setFilteredPointsOfService(filteredPos: List<PointsOfService>?) {
        _filteredPointsOfService = filteredPos
    }

    fun getFilteredPointsOfService(): List<PointsOfService>? {
        return _filteredPointsOfService
    }

    fun getPos(): List<PointsOfService>? {
        return _pointsOfService
    }

    fun setFilteredOrders(filteredOrder: List<Order>?) {
        _filteredOrders = filteredOrder
    }

    fun getFilteredOrders(): List<Order>? {
        return _filteredOrders
    }

    fun setDeliveryAddress(deliveryAddress: ApiResponse<OrderInfo>?) {
        _deliveryAddress = deliveryAddress?.data?.deliveryAddresses
    }

    fun getDeliveryAddresses(): List<DeliveryAddress>? {
        return _deliveryAddress
    }

    fun setDeliveryAddressName(deliveryAddressName: String) {
        _deliveryAddressName.value = deliveryAddressName
    }

    fun setOrderingGroupName(orderingGroupName: String?) {
        _orderingGroupName.value = orderingGroupName
    }

    fun setOrderingGroupNo(orderingGroupNo: String?) {
        _orderingGroupNo.value = orderingGroupNo
    }


    fun setOrderingGroups(orderingGroups: ApiResponse<OrderInfo>?) {
        _orderingGroups = orderingGroups?.data?.orderingGroups
    }

    fun getOrderingGroups(): List<OrderingGroup>? {
        return _orderingGroups
    }

    fun setOrderRowsItem(orderRow: OrderRowsItem) {
        _orderRowsItem.value = _orderRowsItem.value ?: ArrayList()
        // performs find to check if the current OrderRow already exists in the list.
        val articleExits = _orderRowsItem.value?.find { it?.articleNo == orderRow.articleNo }
        // applies change to already existing record
        articleExits?.apply {
            qty = orderRow.qty
            _orderRowsItem.value = _orderRowsItem.value
        }
        // inserts OrderRow if it does not already exist.
        if (articleExits?.articleNo == null) {
            val currentList = _orderRowsItem.value?.toMutableList()
            currentList?.add(orderRow)
            _orderRowsItem.value = currentList
        }
    }

    fun getOrderRowsItem(): List<OrderRowsItem?>? {
        return _orderRowsItem.value
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