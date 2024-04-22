package com.elis.orderingapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.elis.orderingapplication.adapters.ArticleAdapter
import com.elis.orderingapplication.adapters.ArticleEntryAdapter
import com.elis.orderingapplication.databinding.FragmentArticleBinding
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.viewModels.ArticleViewModel
import com.elis.orderingapplication.viewModels.ParamsViewModel


class ArticleFragment : Fragment() {

    private lateinit var binding: FragmentArticleBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private val articleViewModel: ArticleViewModel by activityViewModels()
    private val args: ArticleFragmentArgs by navArgs()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_article, container, false)

        binding.sharedViewModel = sharedViewModel
        binding.articleViewModel = articleViewModel
        binding.toolbar.title = getString(R.string.article_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            view?.let { it ->
                Navigation.findNavController(it)
                    .navigate(R.id.action_articleFragment_to_orderFragment)
            }
        }
        viewPager = binding.articleEntryViewpager

        // Sets ordering group and ordering name to shared ViewModel
        //sharedViewModel.setOrderingGroupNo(args.orderingGroupNo)
        //sharedViewModel.setOrderingGroupName(args.orderingGroupName)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.articleEntry
        viewPager = binding.articleEntryViewpager



        //val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        //val itemSpacingDecoration = CardViewDecoration(spacingInPixels)
        //recyclerView.addItemDecoration(itemSpacingDecoration)

        val order = sharedViewModel.getFilteredOrders()

        val articles: List<Article>? = order?.flatMap { it.articles!!.toList() }

        sharedViewModel.setArticleTotal(articles?.size)

        val iterator = articles?.listIterator()
        while(iterator!!.hasNext()) {
            val i = iterator.next()
            i.totalArticles = articles.size
        }

        val viewPagerAdapter = ArticleEntryAdapter(articles)
        viewPagerAdapter.submitList(articles)
        viewPager = binding.articleEntryViewpager
        //binding.articleEntryViewpager.adapter = viewPagerAdapter
        viewPager.adapter = viewPagerAdapter
        viewPagerAdapter.submitList(articles)

        val adapter =
            ArticleAdapter(ArticleAdapter.ArticleListener { article ->
                articleViewModel.onArticleClicked(article)
                articleViewModel.navigateToArticle.observe(
                    viewLifecycleOwner,
                    Observer { article ->
                        article?.let {
                            this.findNavController().navigate(
                                ArticleFragmentDirections.actionArticleFragmentToOrderFragment()
                            )
                            articleViewModel.onArticleNavigated()
                        }
                    })
            })
        adapter.submitList(articles)

        binding.articleEntry.adapter = adapter
        recyclerView.adapter = adapter


    }



}