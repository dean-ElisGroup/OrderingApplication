package com.elis.orderingapplication.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.Order
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.LocalDate
import java.time.format.DateTimeParseException
import kotlinx.coroutines.flow.first

//class ArticleViewModel: ViewModel(){
class ArticleViewModel(application: Application, private val sharedViewModel: ParamsViewModel) : AndroidViewModel(application) {

    private val _navigateToArticle = MutableLiveData<Article?>()
    val navigateToArticle
        get() = _navigateToArticle

    val database = OrderInfoDatabase.getInstance(application)
    //val articles: LiveData<List<Article>> = database.orderInfoDao.getArticles(getDeliveryDate().value.toString(),getOrderId().value.toString())
    //val order: LiveData<List<Order>> = database.orderInfoDao.getOrderByOrderId(getOrderId().value.toString())

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles

    private val _order = MutableLiveData<List<Order>>()
    val order: LiveData<List<Order>> = _order

    val orderDate: LiveData<String> = order.asFlow().map { orderList ->
        val order = orderList.firstOrNull()
        val inputDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()) // Updated format
        val outputDateFormat = DateTimeFormatter.ofPattern("EEEE,MMMM,dd,yyyy", Locale.getDefault())
        order?.let {
            try {
                val localDate = LocalDate.parse(it.orderDate, inputDateFormat)
                outputDateFormat.format(localDate)
            } catch (e: DateTimeParseException) {
                // Handle the exception
                Log.e("ArticleViewModel", "Error parsing date: ${it.orderDate}", e)
                ""
            }
        } ?: ""
    }.asLiveData()

    init {
        viewModelScope.launch {
            _articles.value =database.orderInfoDao.getArticles(getDeliveryDate().value.toString(), getOrderId().value.toString()).asFlow().first()
            _order.value = database.orderInfoDao.getOrderByOrderId(getOrderId().value.toString()).asFlow().first()
        }
    }




    /*override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            _articles.value = database.orderInfoDao.getArticles(getDeliveryDate().value.toString(), getOrderId().value.toString()).asFlow().first()
            _order.value = database.orderInfoDao.getOrderByOrderId(getOrderId().value.toString()).asFlow().first()
        }
    }*/

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