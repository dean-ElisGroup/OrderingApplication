package com.elis.orderingapplication.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.OrderingLoginResponseStruct
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import kotlinx.coroutines.launch
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LoginViewModel(val loginRep: UserLoginRepository) : ViewModel() {
    val userLoginResponse: MutableLiveData<ApiResponse<OrderingLoginResponseStruct>?> =
        MutableLiveData()

    fun getUserLogin(loginRequest: LoginRequest) = viewModelScope.launch {
        userLoginResponse.postValue(ApiResponse.Loading())
        val response = loginRep.getUserLogin(loginRequest)
        userLoginResponse.postValue(handleUserLoginResponse(response))
    }

    private fun handleUserLoginResponse(response: Response<OrderingLoginResponseStruct>): ApiResponse<OrderingLoginResponseStruct>? {
        if (response.isSuccessful && response.body()?.message == "") {
            response.body()?.let { resultResponse ->
                return ApiResponse.Success(resultResponse)
            }
        }

        if (response.isSuccessful && response.body()?.message != "") {
            response.body()?.let { resultResponse ->
                return ApiResponse.ErrorLogin(resultResponse.message)
            }
        }
        return ApiResponse.Error(response.message())
    }

    fun getDate(): String? {
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM, dd, yyyy")
        return LocalDateTime.now().format(formatter)
    }
}


