package com.elis.orderingapplication.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elis.solstockorder.interfaces.BaseResponse
import com.elis.solstockorder.model.LoginRequest
import com.elis.solstockorder.model.OrderingRequest
import com.elis.solstockorder.pojo2.OrderInfo
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.elis.solstockorder.model.LoginResponse


class LoginViewModel(application: Application) :
    AndroidViewModel(application) {

    //private val userRepo = DeliveryAddressRepository()

    val loginResult: MutableLiveData<BaseResponse<OrderInfo>> = MutableLiveData()
    val login: MutableLiveData<BaseResponse<LoginResponse>> by lazy { MutableLiveData<BaseResponse<LoginResponse>>() }

    fun clearSessionData() {
        login.value = null
    }

    fun getOrderingData(orderSessionKey: OrderingRequest) {
        viewModelScope.launch {
            /*try {
                val response = userRepo.getOrderInfoData(orderSessionKey)
                if (response?.code() == 200) {
                    loginResult.value = BaseResponse.Success(response.body())
                } else {
                    loginResult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                loginResult.value = BaseResponse.Error(ex.message)
            }*/
        }
    }

    /*fun insertData(model: OrderTable) {
        GlobalScope.launch {
            buildDatabase(getApplication<Application>().applicationContext).deliveryAddressDao().insertOrderInfo(
                model
            )
        }
    }

    fun clearData() {
        GlobalScope.launch {
            buildDatabase(getApplication<Application>().applicationContext).orderInfoDao.clearAllData()
        }
    }*/

    fun getSessionKey(loginRequest: LoginRequest) {
        viewModelScope.launch {
            /*try {
                val response = userRepo.getLoginSessionKey(loginRequest)
                if (response?.code() == 200 && response?.body()?.message == "") {
                    login.value =
                        BaseResponse.Success(response.body()) //response.body()!!.sessionKey
                } else {
                    login.value = BaseResponse.ErrorLogin(response?.body())
                }
            } catch (ex: Exception) {
                loginResult.value = BaseResponse.Error(ex.message)
            }*/
        }

    }

    fun getDate(): String? {
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM, dd, yyyy")
        return LocalDateTime.now().format(formatter)
    }
}

