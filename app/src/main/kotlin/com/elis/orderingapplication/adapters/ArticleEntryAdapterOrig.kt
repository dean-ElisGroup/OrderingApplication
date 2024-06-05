package com.elis.orderingapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.databinding.FragmentArticleEntryViewpagerBinding
import com.elis.orderingapplication.pojo2.OrderRowsItem

class ArticleEntryAdapterOrig(private val articles: List<Article>?):
    ListAdapter<Article, ArticleEntryAdapterOrig.ArticleEntryViewHolder>(DiffCallback) {

    class ArticleEntryViewHolder(private var binding: FragmentArticleEntryViewpagerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(articles: Article) {
            binding.article = articles
            binding.articleDescription.text = articles.articleDescription
            binding.articleNo.text = articles.articleNo
            val articlePosition = layoutPosition + 1
            val articleSize = articles.totalArticles
            binding.articlePosition.text = articlePosition.toString()
            binding.ofArticlePosition.text = articleSize.toString()
            binding.executePendingBindings()

            binding.countedQty.doAfterTextChanged {
                articles.solCountedQty = binding.countedQty.text.toString().toIntOrNull()
                val resultOrderQty = articles.articleTargetQty?.let { targetQty ->
                    articles.solCountedQty?.let { countedQty ->
                        //binding.countedQty.text.toString().toIntOrNull()?.let { countedQty ->
                        targetQty - countedQty
                    } ?: run {
                        targetQty
                    }
                } ?: run {
                    0
                }
                binding.orderQty.text = "$resultOrderQty"
                articles.solOrderQty = "$resultOrderQty".toIntOrNull()
            }

            if(articlePosition == articleSize) {
                binding.lastArticleText.visibility = View.VISIBLE
                binding.sendOrderButton.visibility = View.VISIBLE
            }

            val articleToSend = OrderRowsItem(articles.articleSize,articles.solOrderQty,articles.articleNo)
            //articleList.add(articleToSend)
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
    ): ArticleEntryViewHolder {
        return ArticleEntryViewHolder(
            FragmentArticleEntryViewpagerBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int = articles!!.size
    override fun onBindViewHolder(holder: ArticleEntryViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }



}

