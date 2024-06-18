package com.elis.orderingapplication.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.constants.Constants
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.OrderEvent
import com.elis.orderingapplication.pojo2.OrderEventResponse
import com.elis.orderingapplication.pojo2.OrderRowsItem
import com.elis.orderingapplication.pojo2.SendOrder
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Response

class ArticleEntryViewModel(
    application: Application,
    private val loginRep: UserLoginRepository,
    private val sharedViewModel: ParamsViewModel
) : AndroidViewModel(application) {

    private val orderEventResponse: MutableLiveData<ApiResponse<OrderEventResponse>?> =
        MutableLiveData()
    private val _solOrderQty = MutableLiveData<String?>()
    val solOrderQty: MutableLiveData<String?>
        get() = _solOrderQty

    val changeMargin = MutableLiveData<Boolean?>()
    private val marginChange: MutableLiveData<Boolean?>
        get() = changeMargin

    private val _uiState = MutableLiveData<String>()
    val uiState: LiveData<String> = _uiState

    val database = OrderInfoDatabase.getInstance(application)
    val articles: LiveData<List<Article>> = database.orderInfoDao.getArticles(
        getDeliveryDate().value.toString(),
        getOrderId().value.toString()
    )
    val order: LiveData<List<Order>> =
        database.orderInfoDao.getOrderByOrderId(getOrderId().value.toString())

    private fun getDeliveryDate(): LiveData<String> {
        return sharedViewModel.getArticleDeliveryDate()
    }

    private fun getOrderId(): LiveData<String> {
        return sharedViewModel.getArticleAppOrderId()
    }

    fun sendOrderToSOL(order: Order, sendOrderExternalOrderId: String) {
        viewModelScope.launch {
            val sendOrder = SendOrder(
                order.appPosNo.toIntOrNull(),
                order.deliveryAddressNo.toIntOrNull(),
                sendOrderExternalOrderId,
                order.deliveryDate,
                sendOrderExternalOrderId,
                articleRows(order.deliveryDate, order.appOrderId)
            )
            Log.d(
                "Send Order:",
                "$sendOrder"
            )
        }
    }

    private fun articleRows(orderDate: String?, orderId: String?): List<OrderRowsItem> =
        runBlocking {
            withContext(Dispatchers.IO) {
                database.orderInfoDao.getSendOrderArticles(orderDate, orderId)
            }
        }

    fun updateTextByOtherField(
        orderQty: Int,
        countedQty: Int,
        articleNum: String,
        appOrderId: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                withContext(Dispatchers.IO) {
                    orderQty?.let { it1 ->
                        articleNum?.let { it2 ->
                            appOrderId.let { it3 ->
                                database.orderInfoDao.updateOrderQty(
                                    it1,
                                    it2,
                                    it3,
                                    countedQty
                                )
                            }
                        }
                    }
                }
            }
            withContext(Dispatchers.Main) {
                _uiState.value = orderQty.toString()
            }
        }
    }

    //fun show(currentArticle: String?, totalArticles: String?) {
    //    changeMargin.value = currentArticle == totalArticles
    // }

    //fun getShow(): Boolean?{
    //    return marginChange.value
    //}

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