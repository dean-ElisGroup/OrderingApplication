package com.elis.orderingapplication.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.pojo2.PointsOfService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PosViewModel(application: Application, private val sharedViewModel: ParamsViewModel) : AndroidViewModel(application) {

    private val _navigateToPos = MutableLiveData<PointsOfService?>()
    val navigateToPos
        get() = _navigateToPos

    val database = OrderInfoDatabase.getInstance(application)
    val pointOfService: LiveData<List<PointsOfService>> = database.orderInfoDao.getPointsOfService(getDeliveryAddressNum().value.toString(), getOrderingGroupNum().value.toString())
    val posCount: LiveData<Int> = database.orderInfoDao.getOrderCountForPointsOfService(
        getDeliveryAddressNum().value.toString(),
        getOrderingGroupNum().value.toString()
    )
    // Delivery address number, for testing purposes
    // 3390196

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