package com.elis.orderingapplication.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.DelvPosJoin
import com.elis.orderingapplication.pojo2.JoinOrderingGroup
import com.elis.orderingapplication.pojo2.OrderingGroup
import com.elis.orderingapplication.pojo2.PointsOfService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderingGroupViewModel(application: Application) : AndroidViewModel(application) {

    private val _navigateToPosGroup = MutableLiveData<JoinOrderingGroup?>()

    val navigateToOrderingGroup
        get() = _navigateToPosGroup

    val database = OrderInfoDatabase.getInstance(application)
    val orderingGroup: LiveData<List<JoinOrderingGroup>> = database.orderInfoDao.getOrderingGroupList("3390196")
/*
    private val _delvPosJoin = MutableLiveData<DelvPosJoin>()
    val delvPosJoin: LiveData<DelvPosJoin> = _delvPosJoin

    fun getOrderingGroupList(deliveryAddressNo: String) {
        viewModelScope.launch {
            val delvPosJoinData = withContext(Dispatchers.IO) {
                orderInfoDao.getOrderingGroupList(deliveryAddressNo)
            }
            _delvPosJoin.postValue(delvPosJoinData)
        }
    }*/


    fun onOrderingGroupClicked(orderingGroup: JoinOrderingGroup) {
        _navigateToPosGroup.value = orderingGroup
    }

    fun onOrderingGroupNavigated() {
        _navigateToPosGroup.value = null
    }


}