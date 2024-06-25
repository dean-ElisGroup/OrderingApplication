package com.elis.orderingapplication.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.sendOrder.SendOrderViewModel

class SharedViewModelFactory(
    private val sharedViewModel: ParamsViewModel,
    private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OrderingGroupViewModel::class.java)) {
                return OrderingGroupViewModel(application, sharedViewModel) as T
            }
            if (modelClass.isAssignableFrom(PosViewModel::class.java)) {
                return PosViewModel(application, sharedViewModel) as T
            }
            if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
                return OrderViewModel(application, sharedViewModel) as T
            }
            if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
                return ArticleViewModel(application, sharedViewModel) as T
            }
            if (modelClass.isAssignableFrom(SendOrderViewModel::class.java)) {
                return SendOrderViewModel(application, sharedViewModel) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }