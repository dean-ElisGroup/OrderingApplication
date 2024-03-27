package com.elis.orderingapplication.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PosViewModel: ViewModel(){

    private val _navigateToPos = MutableLiveData<String?>()
    val navigateToPos
        get() = _navigateToPos
    fun onPosClicked(orderingGroup: String?) {
        _navigateToPos.value = orderingGroup
    }
    fun onPosNavigated() {
        _navigateToPos.value = null
    }

}