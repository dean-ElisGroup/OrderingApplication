package com.elis.orderingapplication.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.OrderEvent
import com.elis.orderingapplication.pojo2.OrderEventResponse
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class ArticleEntryViewModel(private val loginRep: UserLoginRepository) : ViewModel() {

    val orderEventResponse: MutableLiveData<ApiResponse<OrderEventResponse>?> =
        MutableLiveData()
    private val _solOrderQty = MutableLiveData<String?>()
    val solOrderQty: MutableLiveData<String?>
        get() = _solOrderQty

    val changeMargin = MutableLiveData<Boolean?>()
    private val marginChange: MutableLiveData<Boolean?>
        get() = changeMargin


    fun show(currentArticle: String?, totalArticles: String?) {
        changeMargin.value = currentArticle == totalArticles
    }

    fun getShow(): Boolean?{
        return marginChange.value
    }

    fun updateOrderStatus(order: Order) {
        order.appOrderStatus = "Submitted"
    }

    fun orderEvent(orderEvent: OrderEvent) = viewModelScope.launch {
        orderEventResponse.postValue(ApiResponse.Loading())
        val response = loginRep.sendOrderEvent(orderEvent)
        orderEventResponse.postValue(handleSendOrderResponse(response))
    }

    private fun handleSendOrderResponse(response: Response<OrderEventResponse>): ApiResponse<OrderEventResponse> {
        if (response.isSuccessful && response.body()?.messages!!.isEmpty()) {
            response.body()?.let { resultResponse ->
                return ApiResponse.Success(resultResponse)
            }
        }

        if (response.isSuccessful && response.body()?.messages!!.isNotEmpty()) {
            response.body()?.let { errorResultResponse ->
                return ApiResponse.ErrorLogin(errorResultResponse.messages.toString())
            }
        }
        return ApiResponse.Error(response.message())
    }

    fun updateSolOrderQty(orderQty: String?) {
        _solOrderQty.value = orderQty

    }
}