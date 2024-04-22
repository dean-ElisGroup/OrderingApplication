package com.elis.orderingapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.adapters.ArticleAdapter
import com.elis.orderingapplication.databinding.FragmentArticleEntryViewpagerBinding
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.viewModels.ArticleViewModel
import com.elis.orderingapplication.viewModels.ParamsViewModel

class ArticleEntryFragment1 : Fragment() {

    private lateinit var binding: FragmentArticleEntryViewpagerBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private val articleViewModel: ArticleViewModel by activityViewModels()
    private val args: ArticleFragmentArgs by navArgs()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_article_entry_viewpager,
                container,
                false
            )
        recyclerView = binding.articleEntryRecyclerview
        binding.sharedViewModel = sharedViewModel
        binding.articleViewModel = articleViewModel
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.articleEntryRecyclerview
        val order = sharedViewModel.getFilteredOrders()
        val articles: List<Article>? = order?.flatMap { it.articles!!.toList() }
        sharedViewModel.setArticleTotal(articles?.size)


    }


}