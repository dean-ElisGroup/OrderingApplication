package com.solstockorder.dev.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.model.LoginResponse
import com.elis.orderingapplication.repositories.UserLoginRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.security.auth.callback.Callback


class LoginViewModel constructor(private val repository: UserLoginRepository) : ViewModel() {

    val userLoginResponse = MutableLiveData<List<LoginResponse>>()
    val errorMessage = MutableLiveData<String>()


    fun getUserLoginResponse() {
        viewModelScope.launch {
            val response = repository.getUserLogin()
            response.enqueue(object : retrofit2.Callback<List<LoginResponse>> {
                override fun onResponse(
                    call: Call<List<LoginResponse>>,
                    response: Response<List<LoginResponse>>
                ) {
                    userLoginResponse.postValue(response.body())
                }

                override fun onFailure(call: Call<List<LoginResponse>>, t: Throwable) {
                    errorMessage.postValue(t.message)
                }
            })
        }
    }

    fun getDate(): String? {
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM, dd, yyyy")
        return LocalDateTime.now().format(formatter)
    }
}
