package com.elis.orderingapplication.viewModels

import com.elis.orderingapplication.utils.NetworkErrorException
import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.OrderingLoginResponseStruct
import com.elis.orderingapplication.model.OrderingRequest
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.OrderInfo
import com.elis.orderingapplication.pojo2.OrderingGroup
import com.elis.orderingapplication.repositories.AppRepository
import com.elis.orderingapplication.utils.ApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.lang.ref.WeakReference
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LoginViewModel(private val loginRep: AppRepository) : ViewModel() {
    val userLoginResponse: MutableLiveData<ApiResponse<OrderingLoginResponseStruct>?> =
        MutableLiveData()
    val orderInfoResponse: MutableLiveData<ApiResponse<OrderInfo>?> =
        MutableLiveData()

    private val _showErrorMessageEvent = MutableLiveData<String>()
    val showErrorMessageEvent = _showErrorMessageEvent
    private val _showErrorEvent = MutableLiveData<Boolean>() // Or use a Flow

    private var rootViewRef: WeakReference<View>? = null
    private var orderInfoLoadingRef: WeakReference<View>? = null


    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCleared() {
        super.onCleared()
        rootViewRef?.clear()
        orderInfoLoadingRef?.clear()
    }

    fun getUserLogin(loginRequest: LoginRequest) = viewModelScope.launch {
        try {
            userLoginResponse.postValue(ApiResponse.Loading())
            val response = loginRep.getUserLogin(loginRequest)
            userLoginResponse.postValue(handleUserLoginResponse(response))
        } catch (e: NetworkErrorException) {
            // Handle network error, e.g., display an error message
            _showErrorEvent.postValue(true)
            withContext(Dispatchers.Main) {
                _showErrorMessageEvent.postValue(e.message ?: "Network error")
            }
        }
    }

    fun getOrderInfo(sessionKey: OrderingRequest) = viewModelScope.launch {
        orderInfoResponse.postValue(ApiResponse.Loading())
        when (val response = loginRep.getOrderInfo(sessionKey)) {
            is ApiResponse.Success -> {
                handleSuccessfulOrderInfoResponse(response.data)
            }

            is ApiResponse.NetworkError -> {
                withContext(Dispatchers.Main) {
                    _showErrorMessageEvent.postValue(response.message ?: "Network error")
                }
            }

            else -> {
                response.message?.let {
                    _showErrorMessageEvent.postValue(it)
                }
            }
        }
    }

    private fun handleSuccessfulOrderInfoResponse(orderInfo: OrderInfo?) {
        val transformedOrderInfo = orderInfo?.let { OrderInfoTransformer.transformOrderInfo(it) }
        orderInfoResponse.postValue(handleOrderInfoResponse(transformedOrderInfo))
    }

    object OrderInfoTransformer {
        fun transformOrderInfo(orderInfo: OrderInfo): OrderInfo {
            val orderingGroupResponse = orderInfo.orderingGroups

            orderInfo.deliveryAddresses?.forEach { deliveryAddress ->
                deliveryAddress.pointsOfService?.forEach { pointsOfService ->
                    // Update fields within the PointsOfService object
                    pointsOfService.deliveryAddressNo = deliveryAddress.deliveryAddressNo
                    pointsOfService.deliveryAddressName = deliveryAddress.deliveryAddressName
                    pointsOfService.orderingGroupDescription =
                        orderingGroupResponse?.firstOrNull { it.orderingGroupNo == pointsOfService.pointOfServiceOrderingGroupNo }?.orderingGroupDescription

                    // Iterate over the orders list within each PointsOfService
                    pointsOfService.orders?.forEach { order ->
                        // Update fields within the Order object
                        order.totalArticles = order.articles?.size
                        order.appPosNo = pointsOfService.pointOfServiceNo
                        order.posName = pointsOfService.pointOfServiceName
                        order.deliveryAddressNo = pointsOfService.deliveryAddressNo
                        order.deliveryAddressName = pointsOfService.deliveryAddressName

                        // Iterate over the articles list within each Order
                        order.articles?.forEach { article ->
                            // Update fields within the Article object
                            article.pointOfService = order.appPosNo
                            article.appOrderId = order.appOrderId
                            article.deliveryDate = order.deliveryDate
                            article.orderDate = order.orderDate.toString()
                            article.deliveryAddressNo = order.deliveryAddressNo
                            article.deliveryAddressName = order.deliveryAddressName
                        }
                    }
                }
            }

            return orderInfo
        }
    }

    fun insertToDatabase(
        context: Context,
        addressList: List<DeliveryAddress>?,
        orderingGroupList: List<OrderingGroup>
    ) {
        coroutineScope.launch {
            if (addressList != null) {
                val database = OrderInfoDatabase.getInstance(context)
                database.clearAllTables()
                val dao = database.orderInfoDao

                dao.insert(addressList)
                dao.insertOrderingGroup(orderingGroupList)

                addressList.forEach { deliveryAddress ->
                    deliveryAddress.pointsOfService?.let { pointsOfServiceList ->
                        dao.insertPos(pointsOfServiceList)

                        pointsOfServiceList.forEach { pointsOfService ->
                            pointsOfService.orders?.let { orders ->
                                dao.insertOrder(orders)

                                orders.forEach { order ->
                                    order.articles?.let { articles ->
                                        dao.insertArticle(articles)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleUserLoginResponse(response: Response<OrderingLoginResponseStruct>): ApiResponse<OrderingLoginResponseStruct> {
        userLoginResponse.value = null
        return when {
            response.isSuccessful && response.body()?.message?.isEmpty() == true -> {
                ApiResponse.Success(response.body())
            }

            response.isSuccessful && response.body()?.message == "Login failed" -> {
                ApiResponse.ErrorLogin(response.body()!!.message, response.body())
            }

            else -> {
                ApiResponse.UnknownError("Unknown error occurred")
            }
        }
    }

    private fun handleOrderInfoResponse(response: OrderInfo?): ApiResponse<OrderInfo> {
        if (response?.deliveryAddresses?.isNotEmpty() == true) {
            response.let { resultResponse ->
                return ApiResponse.Success(resultResponse)
            }
        }
        return ApiResponse.NoDataError("There was an issue loading data. Please try again.")
    }

    fun getDate(): String? {
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM, dd, yyyy")
        return LocalDateTime.now().format(formatter)
    }

    fun resetLoginState(viewLifecycleOwner: LifecycleOwner) {
        // Update the LiveData or Flow emitting the login state to a neutral or initial state
        userLoginResponse.value = null
        userLoginResponse.removeObservers(viewLifecycleOwner)
    }
}


