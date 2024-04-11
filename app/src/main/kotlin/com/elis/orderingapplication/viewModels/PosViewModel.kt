package com.elis.orderingapplication.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elis.orderingapplication.pojo2.PointsOfService

class PosViewModel: ViewModel(){

    private val _navigateToPos = MutableLiveData<PointsOfService?>()
    val navigateToPos
        get() = _navigateToPos
    fun onPosClicked(pointsOfService: PointsOfService?) {
        _navigateToPos.value = pointsOfService
    }
    fun onPosNavigated() {
        _navigateToPos.value = null
    }

}