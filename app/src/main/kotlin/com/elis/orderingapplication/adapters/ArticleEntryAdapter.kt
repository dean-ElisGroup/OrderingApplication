package com.elis.orderingapplication.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.elis.orderingapplication.ArticleEntryCardFragment
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.ArticleParcelable

class ArticleEntryAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    //private var articles: List<Article>,
    private var articles:MutableList<Article> = mutableListOf(),

    ) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = articles.size

    override fun createFragment(position: Int): Fragment {

        //val articles = articles[position]
        val entryFragment = ArticleEntryCardFragment()
        val articlePosition = position + 1
        //val updatedArticleList = parcelizeArticle(articles)

        val fragmentBundle = Bundle().apply {
            //putParcelable("article", updatedArticleList)
            putString("numberOfArticles", itemCount.toString())
            putInt("currentArticlePosition", articlePosition)
            putInt("currentArticle", position)
            putInt("totalArticles", itemCount)
        }
        entryFragment.arguments = fragmentBundle

        return entryFragment
    }

    private fun parcelizeArticle(articlesList: Article): ArticleParcelable {
        val articleParcelable = ArticleParcelable(
            articlesList.articleNo,
            articlesList.articleDescription,
            articlesList.articleSize,
            articlesList.articleTargetQty,
            articlesList.articleMinQty,
            articlesList.articleMaxQty,
            articlesList.articleIntervalQty,
            articlesList.solOrderQty,
            articlesList.solCountedQty,
            articlesList.totalArticles,
            articlesList.pointOfService,
            articlesList.deliveryDate,
            articlesList.orderDate,
            articlesList.appOrderId,
            articlesList.deliveryAddressNo,
            articlesList.deliveryAddressName
        )

        return articleParcelable
    }

    fun updateData(newData: List<Article>) {
        //articles = newData
        articles = newData.toMutableList()
        notifyDataSetChanged()
    }

    fun clearArticles() {
        articles.clear()
        notifyDataSetChanged()
    }
}