package com.elis.orderingapplication.viewModels

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import com.elis.orderingapplication.repositories.AppRepository

class LoginViewModelFactory(private val loginRepository: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(loginRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

