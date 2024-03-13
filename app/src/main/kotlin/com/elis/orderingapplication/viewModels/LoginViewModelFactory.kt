package com.elis.orderingapplication.viewModels

import androidx.lifecycle.ViewModelProvider
import android.app.Application
import androidx.lifecycle.ViewModel
import com.elis.orderingapplication.repositories.UserLoginRepository

class LoginViewModelFactory(
    val loginRepository: UserLoginRepository): ViewModelProvider.Factory {
override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return LoginViewModel(loginRepository) as T
}

}

