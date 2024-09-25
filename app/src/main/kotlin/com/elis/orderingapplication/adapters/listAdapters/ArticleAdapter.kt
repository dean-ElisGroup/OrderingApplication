package com.elis.orderingapplication.adapters.listAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.ArticleCardviewBinding
import com.elis.orderingapplication.pojo2.Article

class ArticleAdapter(private val clickListener: ArticleAdapter.MyClickListener) :
    ListAdapter<Article, ArticleAdapter.ArticlesViewHolder>(DiffCallback) {

    private var data: List<Article> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticlesViewHolder {
        return ArticlesViewHolder(
            ArticleCardviewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            // Return true if the items represent the same entity (e.g., have the same ID)
            return oldItem.solOrderQty == newItem.solOrderQty
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            // Return true if the contents of the items are the same
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: ArticlesViewHolder, position: Int) {
        val articles = data[position]
        holder.bind(clickListener, articles)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    fun setData(newData: List<Article>) {
        data = newData
        notifyDataSetChanged()
    }


    interface MyClickListener {
        fun onItemClick(myData: Article)
    }


    class ArticlesViewHolder(private var binding: ArticleCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: MyClickListener, articles: Article) {
            binding.article = articles
            binding.clickListener = clickListener
            val articlePosition = layoutPosition + 1
            binding.articlePosition.text = articlePosition.toString()
            binding.executePendingBindings()
        }
    }
}