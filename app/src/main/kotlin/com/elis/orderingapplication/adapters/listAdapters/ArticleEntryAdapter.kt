package com.elis.orderingapplication.adapters.listAdapters

import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.elis.orderingapplication.ArticleEntryCardFragment
import com.elis.orderingapplication.databinding.ArticleCardviewBinding
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.viewModels.ParamsViewModel

//class ArticleEntryAdapter(private val articleList: List<Article>) : RecyclerView.Adapter<ArticleEntryAdapter.ArticleListViewHolder>() {
class ArticleEntryAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, private val articleList: List<Article>) : FragmentStateAdapter(fragmentManager, lifecycle) {
    /*override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleEntryAdapter.ArticleListViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(
        holder: ArticleEntryAdapter.ArticleListViewHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }*/

    override fun getItemCount(): Int = articleList.size



    override fun createFragment(position: Int): Fragment {
        val fragment = ArticleEntryCardFragment()
        fragment.arguments = Bundle().apply {
            putInt("currentArticlePosition", position)
            putInt("currentArticle", position)
            putInt("totalArticles", itemCount)
        }
        return fragment
    }

    /*class ArticleListViewHolder(private var binding: ArticleCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(articles: Article) {
            binding.article = articles
            binding.executePendingBindings()
        }
    }*/
}