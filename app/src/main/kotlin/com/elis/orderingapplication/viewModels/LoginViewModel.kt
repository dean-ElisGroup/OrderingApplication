package com.elis.orderingapplication.viewModels

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.R
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.OrderingLoginResponseStruct
import com.elis.orderingapplication.model.OrderingRequest
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.OrderInfo
import com.elis.orderingapplication.pojo2.OrderingGroup
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LoginViewModel(private val loginRep: UserLoginRepository) : ViewModel() {
    val userLoginResponse: MutableLiveData<ApiResponse<OrderingLoginResponseStruct>?> =
        MutableLiveData()
    val orderInfoResponse: MutableLiveData<ApiResponse<OrderInfo>?> =
        MutableLiveData()

    lateinit var rootView: View
    lateinit var orderInfoLoading: View

    var orderCount = 0

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun getDeviceInfo() {
        val deviceName = Build.MODEL
        val deviceName1 = Build.DEVICE
    }

    fun getUserLogin(loginRequest: LoginRequest) = viewModelScope.launch {
        userLoginResponse.postValue(ApiResponse.Loading())
        val response = loginRep.getUserLogin(loginRequest)
        userLoginResponse.postValue(handleUserLoginResponse(response))
    }

    fun getOrderInfo(sessionKey: OrderingRequest) = viewModelScope.launch {
        orderInfoResponse.postValue(ApiResponse.Loading())
        //val response = loginRep.getOrderInfo(sessionKey)
        when (val response = loginRep.getOrderInfo(sessionKey)) {
            is ApiResponse.Success -> {
                // Handle successful response
                handleSuccessfulOrderInfoResponse(response.data)
            }

            is ApiResponse.Error -> {
                // Display error message to the user
                response.message?.let { showErrorMessage(it) }
            }

            is ApiResponse.NoDataError -> {
                // Display error message to the user
                response.message?.let { showErrorMessage(it) }
            }

            is ApiResponse.UnknownError -> {
                // Display error message to the user
                response.message?.let { showErrorMessage(it) }
            }

            is ApiResponse.ErrorLogin -> TODO()
            is ApiResponse.ErrorSendOrderDate -> TODO()
            is ApiResponse.Loading -> TODO()
        }
    }

    private fun handleSuccessfulOrderInfoResponse(orderInfo: OrderInfo?) {
        val orderingGroupResponse = orderInfo?.orderingGroups

        orderInfo?.deliveryAddresses?.forEach { deliveryAddress ->
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

        orderInfoResponse.postValue(handleOrderInfoResponse(orderInfo))
    }

    private fun showErrorMessage(message: String) {
        orderInfoLoading.visibility = View.GONE
        val builder = AlertDialog.Builder(rootView.context)
        builder.setTitle("Connection error")
            .setIcon(R.drawable.outline_error_24)
            .setMessage("Could not retrieve data. \n\nCheck your internet connection and try again.")
            .setPositiveButton("OK", null)
            .create()
            .show()
    }


    /*}

        val orderingGroupResponse = response?.body()?.orderingGroups

        response?.body()?.deliveryAddresses?.forEach { deliveryAddress ->
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

        orderInfoResponse.postValue(handleOrderInfoResponse(response))
    }*/

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

    /*private fun handleOrderInfoResponse(response: Response<OrderInfo>): ApiResponse<OrderInfo> {
        if (response.isSuccessful && response.body()?.deliveryAddresses?.isNotEmpty() == true) {
            response.body()?.let { resultResponse ->
                return ApiResponse.Success(resultResponse)
            }
        }
        return ApiResponse.NoDataError("There was an issue loading data. Please try again.")
    }*/

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


