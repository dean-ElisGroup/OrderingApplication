package com.elis.orderingapplication.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.elis.orderingapplication.ArticleEntryCardFragment
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.ArticleParcelable
import com.elis.orderingapplication.viewModels.ArticleEntryViewModel
import com.elis.orderingapplication.viewModels.ArticleViewModel
import com.elis.orderingapplication.viewModels.ParamsViewModel


class ArticleEntryAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val articles: List<Article>,
    private val articleEntryViewModel: ArticleEntryViewModel
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    //override fun getItemCount(): Int {
    //    return articles.size
    //}

    private var data: List<Article> = emptyList()
    override fun getItemCount(): Int = articles.size

    override fun createFragment(position: Int): Fragment {


        val articles = articles[position]
        val entryFragment = ArticleEntryCardFragment()
        val articlePosition = position+1

        val articleParcelable = ArticleParcelable(
            articles.articleNo,
            articles.articleDescription,
            articles.articleSize,
            articles.articleTargetQty,
            articles.articleMinQty,
            articles.articleMaxQty,
            articles.articleIntervalQty,
            articles.solOrderQty,
            articles.solCountedQty,
            articles.totalArticles,
            articles.pointOfService,
            articles.deliveryDate,
            articles.orderDate,
            articles.appOrderId,
            articles.deliveryAddressNo,
            articles.deliveryAddressName)


        val fragmentBundle = Bundle().apply {
            putParcelable("article", articleParcelable)
        }
        entryFragment.arguments = fragmentBundle

        //val viewModel = articleEntryViewModel
        //viewModel.updateArticleData(articles)

        //val fragmentBundle = Bundle()
        //fragmentBundle.putString("articleNo", articles.articleNo)
        //fragmentBundle.putString("articleDescription", articles.articleDescription)
        //fragmentBundle.putString("articleTargetQty", articles.articleTargetQty.toString())
        //fragmentBundle.putString("articleSize", articles.articleSize)
        //fragmentBundle.putString("numberOfArticles", itemCount.toString())
        //fragmentBundle.putInt("currentArticlePosition", articlePosition)
        //fragmentBundle.putInt("currentArticle", position)
        //entryFragment.arguments = fragmentBundle

        return entryFragment
    }

    fun updateData(newData: List<Article>) {
        data = newData
        notifyDataSetChanged()
    }
}