package com.elis.orderingapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.ArticleCardviewBinding
import com.elis.orderingapplication.pojo2.Article

class ArticleAdapter(private val clickListener: ArticleListener) :
    ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(DiffCallback) {
    class ArticleViewHolder(private var binding: ArticleCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: ArticleListener, article: Article) {
            binding.article = article
            //binding.clickListener = clickListener
            // Allows calculation of ORDER QTY once a user enters a Counted Qty.
            binding.countedQty.doAfterTextChanged {
                article.solCountedQty = binding.countedQty.text.toString().toIntOrNull()
                val resultOrderQty = article.articleTargetQty?.let { targetQty ->
                    article.solCountedQty?.let { countedQty ->
                    //binding.countedQty.text.toString().toIntOrNull()?.let { countedQty ->
                        targetQty - countedQty
                    } ?: run {
                        targetQty
                    }
                } ?: run {
                    0
                }
                binding.orderQty.text = "$resultOrderQty"
                article.solOrderQty = "$resultOrderQty".toIntOrNull()
            }
            var articlePosition = layoutPosition+1
            var articleSize = article.totalArticles
            binding.articlePosition.text = articlePosition.toString()
            binding.ofArticlePosition.text = articleSize.toString()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: Article,
            newItem: Article
        ): Boolean {
            return oldItem.articleNo == newItem.articleNo
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleViewHolder {
        return ArticleViewHolder(
            ArticleCardviewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(clickListener, article)
    }

    class ArticleListener(val clickListener: (article: Article?) -> Unit) {
        fun onClick(article: Article) =
            clickListener(article)
    }


}

