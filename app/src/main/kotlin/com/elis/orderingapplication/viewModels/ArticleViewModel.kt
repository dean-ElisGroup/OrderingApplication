package com.elis.orderingapplication.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.Order

//class ArticleViewModel: ViewModel(){
class ArticleViewModel(application: Application, private val sharedViewModel: ParamsViewModel) : AndroidViewModel(application) {

    private val _navigateToArticle = MutableLiveData<Article?>()
    val navigateToArticle
        get() = _navigateToArticle

    val database = OrderInfoDatabase.getInstance(application)
    val articles: LiveData<List<Article>> = database.orderInfoDao.getArticles(getDeliveryDate().value.toString(),getOrderId().value.toString())
    val order: LiveData<List<Order>> = database.orderInfoDao.getOrderByOrderId(getOrderId().value.toString())

    private fun getDeliveryDate(): LiveData<String> {
        return sharedViewModel.getArticleDeliveryDate()
    }
    private fun getOrderId(): LiveData<String> {
        return sharedViewModel.getArticleAppOrderId()
    }


    fun onArticleClicked(article: Article?) {
        _navigateToArticle.value = article
    }
    fun onArticleNavigated() {
        _navigateToArticle.value = null
    }

}