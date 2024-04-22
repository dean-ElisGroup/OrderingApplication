package com.elis.orderingapplication.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.PointsOfService

class ArticleViewModel: ViewModel(){

    private val _navigateToArticle = MutableLiveData<Article?>()
    val navigateToArticle
        get() = _navigateToArticle
    fun onArticleClicked(article: Article?) {
        _navigateToArticle.value = article
    }
    fun onArticleNavigated() {
        _navigateToArticle.value = null
    }

}