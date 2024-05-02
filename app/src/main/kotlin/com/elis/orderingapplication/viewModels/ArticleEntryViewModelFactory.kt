package com.elis.orderingapplication.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.elis.orderingapplication.repositories.UserLoginRepository



    class ArticleEntryViewModelFactory(
        private val articleEntryRepository: UserLoginRepository
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ArticleEntryViewModel(articleEntryRepository) as T
        }

    }
