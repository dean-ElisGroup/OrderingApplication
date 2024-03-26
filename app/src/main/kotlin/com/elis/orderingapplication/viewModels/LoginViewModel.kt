package com.elis.orderingapplication.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.OrderingLoginResponseStruct
import com.elis.orderingapplication.model.OrderingRequest
import com.elis.orderingapplication.pojo2.OrderInfo
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import kotlinx.coroutines.launch
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LoginViewModel(private val loginRep: UserLoginRepository) : ViewModel() {
    val userLoginResponse: MutableLiveData<ApiResponse<OrderingLoginResponseStruct>?> =
        MutableLiveData()
    val orderInfoResponse: MutableLiveData<ApiResponse<OrderInfo>?> =
        MutableLiveData()

    fun getUserLogin(loginRequest: LoginRequest) = viewModelScope.launch {
        userLoginResponse.postValue(ApiResponse.Loading())
        val response = loginRep.getUserLogin(loginRequest)
        userLoginResponse.postValue(handleUserLoginResponse(response))


    }

    fun getOrderInfo(sessionKey: OrderingRequest) = viewModelScope.launch {
        orderInfoResponse.postValue(ApiResponse.Loading())
        val response = loginRep.getOrderInfo(sessionKey)
        orderInfoResponse.postValue(handleOrderInfoResponse(response))
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


