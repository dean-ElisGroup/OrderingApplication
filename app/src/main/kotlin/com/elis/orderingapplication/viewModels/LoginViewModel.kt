package com.elis.orderingapplication.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.OrderingLoginResponseStruct
import com.elis.orderingapplication.model.OrderingRequest
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.OrderInfo
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
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

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun getUserLogin(loginRequest: LoginRequest) = viewModelScope.launch {
        userLoginResponse.postValue(ApiResponse.Loading())
        val response = loginRep.getUserLogin(loginRequest)
        userLoginResponse.postValue(handleUserLoginResponse(response))
    }

    fun getOrderInfo(sessionKey: OrderingRequest) = viewModelScope.launch {
        orderInfoResponse.postValue(ApiResponse.Loading())
        val response = loginRep.getOrderInfo(sessionKey)

        response?.body()?.deliveryAddresses?.forEach { deliveryAddress ->
            deliveryAddress.pointsOfService?.forEach { pointsOfService ->
                // Update fields within the PointsOfService object
                pointsOfService.deliveryAddressNo = deliveryAddress.deliveryAddressNo
                pointsOfService.deliveryAddressName = deliveryAddress.deliveryAddressName

                // Iterate over the orders list within each PointsOfService
                pointsOfService.orders?.forEach { order ->
                    // Update fields within the Order object
                    order.totalArticles = order.articles?.size
                    order.appPosNo = pointsOfService.pointOfServiceNo
                    order.posName = pointsOfService.pointOfServiceName

                    // Iterate over the articles list within each Order
                    order.articles?.forEach { article ->
                        // Update fields within the Article object
                        article.pointOfService = order.appPosNo
                        article.deliveryDate = order.deliveryDate
                    }
                }
            }
        }

        orderInfoResponse.postValue(handleOrderInfoResponse(response))
    }

    fun insertToDatabase(context: Context, addressList: List<DeliveryAddress>?) {
        coroutineScope.launch {
            if (addressList != null) {
                val database = OrderInfoDatabase.getInstance(context)
                val dao = database.orderInfoDao

                dao.insert(addressList)

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
        if (response.isSuccessful && response.body()?.message == "") {
            response.body()?.let { resultResponse ->
                return ApiResponse.Success(resultResponse)
            }
        }

        if (response.isSuccessful && response.body()?.message != "") {
            response.body()?.let { errorResultResponse ->
                return ApiResponse.ErrorLogin(errorResultResponse.message)
            }
        }
        return ApiResponse.Error(response.message())
    }

    private fun handleOrderInfoResponse(response: Response<OrderInfo>): ApiResponse<OrderInfo> {
        if (response.isSuccessful && response.body()?.deliveryAddresses?.isNotEmpty() == true) {
            response.body()?.let { resultResponse ->
                return ApiResponse.Success(resultResponse)
            }
        }
        //if (response.isSuccessful && response.body()?.deliveryAddresses?.isNotEmpty() == false) {
        //        return ApiResponse.NoDataError("There was an issue loading data. Please try again.")
        // }
        return ApiResponse.NoDataError("There was an issue loading data. Please try again.")
    }


    fun getDate(): String? {
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM, dd, yyyy")
        return LocalDateTime.now().format(formatter)
    }
}


