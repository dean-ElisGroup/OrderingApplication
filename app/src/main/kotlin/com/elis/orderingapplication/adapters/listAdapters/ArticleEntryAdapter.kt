package com.elis.orderingapplication.adapters.listAdapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.ArticleCardviewBinding
import com.elis.orderingapplication.pojo2.Article

class ArticleEntryAdapter(private val articleList: List<Article>) : RecyclerView.Adapter<ArticleEntryAdapter.ArticleListViewHolder>() {
    override fun onCreateViewHolder(
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
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class ArticleListViewHolder(private var binding: ArticleCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(articles: Article) {
            binding.article = articles
            binding.executePendingBindings()
        }
    }
}