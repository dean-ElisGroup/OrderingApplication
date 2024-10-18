package com.elis.orderingapplication.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.pojo2.PointsOfService
import com.elis.orderingapplication.pojo2.PointsOfServiceWithTotalOrders
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class PosViewModel(application: Application, private val sharedViewModel: ParamsViewModel) :
    AndroidViewModel(application) {

    sealed class PosUiState {
        data object Loading : PosUiState()
        data class Success(val pointsOfService: List<PointsOfServiceWithTotalOrders>) : PosUiState()
        data object Error : PosUiState()
    }

    private val _uiState = MutableStateFlow<PosUiState>(PosUiState.Loading)
    val uiState: StateFlow<PosUiState> = _uiState.asStateFlow()

    private val _navigateToPos = MutableSharedFlow<PointsOfService?>(replay = 1)
    val navigateToPos = _navigateToPos.asSharedFlow()

    private val _pointsOfService =
        MutableStateFlow<List<PointsOfServiceWithTotalOrders>>(emptyList())
    val pointsOfService: StateFlow<List<PointsOfServiceWithTotalOrders>> =
        _pointsOfService.asStateFlow()

    val database = OrderInfoDatabase.getInstance(application)

    init {
        viewModelScope.launch {
            _uiState.value = PosUiState.Loading

            combine(
                deliveryAddressNumber().asFlow().distinctUntilChanged(),
                orderingGroupNumber().asFlow().distinctUntilChanged()
            ) { deliveryAddressNo, orderingGroup ->
                getPointsOfServiceWithTotalOrders(deliveryAddressNo, orderingGroup, getOrderDate())
            }.flatMapMerge { it.asFlow() }
                .catch { exception ->
                    _uiState.value = PosUiState.Error
                    // You can also log the exception here for debugging:
                    Log.e("PosViewModel", "Error loading points of service", exception)
                }
                .collect { pointsOfService ->
                    _pointsOfService.value = pointsOfService
                    _uiState.value =
                        PosUiState.Success(pointsOfService)
                }
        }
    }

    private fun getPointsOfServiceWithTotalOrders(
        deliveryAddressNo: String,
        orderingGroup: String,
        orderDate: String
    ): LiveData<List<PointsOfServiceWithTotalOrders>> {
        return database.orderInfoDao.getPointsOfServiceWithTotalOrders(
            deliveryAddressNo,
            orderingGroup,
            orderDate
        )
    }

    private fun getOrderDate(): String {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    private fun deliveryAddressNumber(): LiveData<String> {
        return sharedViewModel.getDeliveryAddressNumber()
    }

    private fun orderingGroupNumber(): LiveData<String> {
        return sharedViewModel.getOrderingGroupNum()
    }

    fun onPosClicked(pointsOfService: PointsOfService?) {
        viewModelScope.launch {
            _navigateToPos.emit(null)
            _navigateToPos.emit(pointsOfService)
        }
    }

    fun onPosNavigated() {
        viewModelScope.launch {
            _navigateToPos.emit(null)
        }
    }

}

