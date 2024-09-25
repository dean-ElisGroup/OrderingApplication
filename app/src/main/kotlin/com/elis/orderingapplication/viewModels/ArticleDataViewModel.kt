package com.elis.orderingapplication.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elis.orderingapplication.pojo2.Article

class ArticleDataViewModel : ViewModel() {
    private val _articleData = MutableLiveData<Article>()
    val articleData: LiveData<Article> = _articleData

    fun setArticleData(article: Article) {
        _articleData.value = article
    }
}