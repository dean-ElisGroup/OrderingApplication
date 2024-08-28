package com.elis.orderingapplication.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.pojo2.PointsOfService
import com.elis.orderingapplication.pojo2.PointsOfServiceWithTotalOrders
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PosViewModel(application: Application, private val sharedViewModel: ParamsViewModel) : AndroidViewModel(application) {

    private val _navigateToPos = MutableLiveData<PointsOfService?>()
    val navigateToPos
        get() = _navigateToPos

    val database = OrderInfoDatabase.getInstance(application)
    val pointsOfService: LiveData<List<PointsOfServiceWithTotalOrders>> = getPointsOfServiceWithTotalOrders(getDeliveryAddressNum().value.toString(), getOrderingGroupNum().value.toString(), getOrderDate()) // Added to this line LiveData<List<PointsOfServiceWithTotalOrders>>

    private fun getPointsOfServiceWithTotalOrders(
        deliveryAddressNo: String,
        orderingGroup: String,
        orderDate: String
        //orderStatus: List<Int>
    ): LiveData<List<PointsOfServiceWithTotalOrders>> {
        return database.orderInfoDao.getPointsOfServiceWithTotalOrders(
            deliveryAddressNo,
            orderingGroup,
            orderDate
            //orderStatus
        )
    }
    private fun getOrderDate(): String {
        val orderDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val orderDate = LocalDateTime.now().format(orderDateFormatter)
        return orderDate
    }

    // returns the stored delivery address no.
    private fun getDeliveryAddressNum(): LiveData<String> {
        return sharedViewModel.getDeliveryAddressNumber()
    }
    // returns the stored ordering group no.
    private fun getOrderingGroupNum(): LiveData<String> {
        return sharedViewModel.getOrderingGroupNum()
    }

    fun onPosClicked(pointsOfService: PointsOfService?) {
        _navigateToPos.value = pointsOfService
    }
    fun onPosNavigated() {
        _navigateToPos.value = null
    }

}