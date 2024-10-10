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
    private val userRepository: UserLoginRepository,
) : AndroidViewModel(application) {

    val userLogoutResponse: MutableLiveData<ApiResponse<Boolean>> = MutableLiveData()

    fun logoutUser(logoutRequest: LogoutRequest) = viewModelScope.launch {
        val response = userRepository.userLogout(logoutRequest)
        userLogoutResponse.postValue(handleLogoutResponse(response))
    }

    private fun handleLogoutResponse(response: Boolean): ApiResponse<Boolean> {
        return when (response) {
            true -> ApiResponse.Success(response)
            false -> ApiResponse.Error("Logout failed. Please check your network connection or try again later.")
        }
    }
}