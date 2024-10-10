package com.elis.orderingapplication.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.sendOrder.SendOrderViewModel

class AppViewModelFactory(
    private val sharedViewModel: ParamsViewModel,
    private val application: Application,
    private val articleEntryRepository: UserLoginRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArticleEntryViewModel::class.java)) {
            return ArticleEntryViewModel(application, articleEntryRepository, sharedViewModel) as T
        }
        if (modelClass.isAssignableFrom(LandingPageViewModel::class.java)) {
            return LandingPageViewModel(application, articleEntryRepository) as T
        }
        if (modelClass.isAssignableFrom(SendOrderViewModel::class.java)) {
            return SendOrderViewModel(application, articleEntryRepository, sharedViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}



