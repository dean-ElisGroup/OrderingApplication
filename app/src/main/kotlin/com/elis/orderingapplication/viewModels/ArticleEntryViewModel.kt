package com.elis.orderingapplication.viewModels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.constants.Constants.Companion.DATE_TOO_LATE
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.OrderEvent
import com.elis.orderingapplication.pojo2.OrderEventResponse
import com.elis.orderingapplication.pojo2.OrderRowsItem
import com.elis.orderingapplication.pojo2.SendOrder
import com.elis.orderingapplication.repositories.AppRepository
import com.elis.orderingapplication.utils.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Response


class ArticleEntryViewModel(
    private val application: Application,
    private val appRepository: AppRepository,
    private val sharedViewModel: ParamsViewModel
) : ViewModel() {

    private val orderEventResponse: MutableLiveData<ApiResponse<OrderEventResponse>?> =
        MutableLiveData()
    private val _solOrderQty = MutableLiveData<String?>()
    val solOrderQty: MutableLiveData<String?>
        get() = _solOrderQty


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


    fun sendOrderToSOL(order: Order, sendOrderExternalOrderId: String): SendOrder {
        val sendOrder = SendOrder(
            order.appPosNo.toIntOrNull(),
            order.deliveryAddressNo.toIntOrNull(),
            sendOrderExternalOrderId,
            order.deliveryDate,
            sendOrderExternalOrderId,
            articleRows(order.deliveryDate, order.appOrderId)
        )
        return sendOrder
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
                    orderQty.let { it1 ->
                        articleNum.let { it2 ->
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

    fun updateOrderStatus(order: Order, appStatus: String, orderStatus: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                withContext(Dispatchers.IO) {
                    database.orderInfoDao.updateOrderStatus(
                        order.appOrderId,
                        appStatus,
                        orderStatus
                    )
                }
            }
        }
    }

    /*fun orderEvent(orderEvent: OrderEvent) = viewModelScope.launch {
        //orderEventResponse.value = null
        orderEventResponse.postValue(ApiResponse.Loading())
        val response = loginRep.sendOrderEvent(orderEvent)
        orderEventResponse.postValue(handleSendOrderResponse(response))
    }*/

    fun orderEvent(orderEvent: OrderEvent): ApiResponse<OrderEventResponse> = runBlocking {
        orderEventResponse.postValue(ApiResponse.Loading())
        val response = appRepository.sendOrderEvent(orderEvent)
        handleSendOrderResponse(response)
    }

    private fun handleSendOrderResponse(response: Response<OrderEventResponse>): ApiResponse<OrderEventResponse> {
        //orderEventResponse.value = null
        return when {
            response.isSuccessful && response.body()?.success == true && response.body()?.messages?.isEmpty() == true -> {
                ApiResponse.Success(response.body())
        }
            response.isSuccessful && response.body()?.messages?.any {
                it!!.contains(
                    DATE_TOO_LATE,
                    ignoreCase = true
                )
            } == true -> {
                ApiResponse.ErrorSendOrderDate(response.body()?.messages.toString())
            }

            response.isSuccessful && response.body()?.messages?.none {
                it!!.contains(
                    DATE_TOO_LATE,
                    ignoreCase = true
                )
            } == true -> {
                ApiResponse.Error(response.body()?.messages.toString())
            }

            !response.isSuccessful && response.code() == 401 -> {
                ApiResponse.ErrorLogin("Authentication failed")
            }

            !response.isSuccessful && response.body()?.messages?.isNotEmpty() == true -> {
                ApiResponse.Error(response.body()?.messages.toString())
            }

            else -> {
                ApiResponse.UnknownError("Unknown error occurred")
            }
        }
    }

    fun updateSolOrderQty(orderQty: String?) {
        _solOrderQty.value = orderQty

    }
}