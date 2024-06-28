package com.elis.orderingapplication.sendOrder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.constants.Constants
import com.elis.orderingapplication.constants.Constants.Companion.APP_STATUS_SENT
import com.elis.orderingapplication.constants.Constants.Companion.ORDER_STATUS_FINISHED
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.OrderEvent
import com.elis.orderingapplication.pojo2.OrderEventResponse
import com.elis.orderingapplication.pojo2.OrderRowsItem
import com.elis.orderingapplication.pojo2.SendOrder
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import com.elis.orderingapplication.viewModels.ParamsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SendOrderViewModel(application: Application,
private val loginRep: UserLoginRepository,
private val sharedViewModel: ParamsViewModel
) : AndroidViewModel(application) {

    private val _navigateToOrder = MutableLiveData<Order?>()
    val orderEventResponse: MutableLiveData<ApiResponse<OrderEventResponse>?> =
        MutableLiveData()

    val database = OrderInfoDatabase.getInstance(application)
    val orders: LiveData<List<Order>> = database.orderInfoDao.getSendOrders(getDeliveryAddressNum().value.toString(),Constants.APP_STATUS_FINISHED)

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

    fun orderEvent(orderEvent: OrderEvent) = viewModelScope.launch {
        orderEventResponse.value = null
        orderEventResponse.postValue(ApiResponse.Loading())
        val response = loginRep.sendOrderEvent(orderEvent)
        orderEventResponse.postValue(handleSendOrderResponse(response))
    }

    private fun handleSendOrderResponse(response: Response<OrderEventResponse>): ApiResponse<OrderEventResponse> {
        orderEventResponse.value = null
        return when {
            response.isSuccessful && response.body()?.success == true && response.body()?.messages?.isEmpty() == true -> {
                ApiResponse.Success(response.body())
            }

            response.isSuccessful && response.body()?.messages?.any {
                it!!.contains(
                    Constants.DATE_TOO_LATE,
                    ignoreCase = true
                )
            } == true -> {
                ApiResponse.ErrorSendOrderDate(response.body()?.messages.toString())
            }

            response.isSuccessful && response.body()?.messages?.none {
                it!!.contains(
                    Constants.DATE_TOO_LATE,
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

    fun updateOrderStatus(order: Order) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                withContext(Dispatchers.IO){
                    database.orderInfoDao.updateOrderStatus(order.appOrderId,
                        APP_STATUS_SENT.toString(),
                        ORDER_STATUS_FINISHED
                    )
                }
            }
        }
    }



    private fun getOrderDate(): String {
        var orderDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDateTime.now().format(orderDateFormatter)
    }
    private fun getDeliveryAddressNum(): LiveData<String> {
        return sharedViewModel.getDeliveryAddressNumber()
    }
    private fun getPointOfServiceNum(): LiveData<String> {
        return sharedViewModel.getPosNum()
    }

    val navigateToOrder
        get() = _navigateToOrder
    fun onOrderClicked(order: Order?) {
        _navigateToOrder.value = order
    }
    fun onOrderNavigated() {
        _navigateToOrder.value = null
    }

}