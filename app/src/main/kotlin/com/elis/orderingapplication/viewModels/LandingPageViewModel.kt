package com.elis.orderingapplication.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.model.LogoutRequest
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import kotlinx.coroutines.launch


class LandingPageViewModel(
    application: Application,
    private val loginRep: UserLoginRepository,
    private val sharedViewModel: ParamsViewModel
) : AndroidViewModel(application) {

    val userLoginResponse: MutableLiveData<ApiResponse<Boolean>> = MutableLiveData()
    fun getUserLogout(logoutRequest: LogoutRequest)  = viewModelScope.launch {
        val response = loginRep.userLogout(logoutRequest)
        userLoginResponse.postValue(handleUserLoginResponse(response))
    }

    private fun handleUserLoginResponse(response: Boolean): ApiResponse<Boolean> {
        return if (response) {
            ApiResponse.Success(response)
        } else
            ApiResponse.Error("Logout could not be completed")
        }


}