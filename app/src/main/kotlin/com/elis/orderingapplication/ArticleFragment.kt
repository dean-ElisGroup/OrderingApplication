package com.elis.orderingapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.elis.orderingapplication.adapters.ArticleEntryAdapter
import com.elis.orderingapplication.databinding.FragmentArticleBinding
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.viewModels.ArticleEntryViewModel
import com.elis.orderingapplication.viewModels.ArticleEntryViewModelFactory
import com.elis.orderingapplication.viewModels.ArticleViewModel
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.viewModels.SharedViewModelFactory


class ArticleFragment : Fragment() {

    private lateinit var binding: FragmentArticleBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: ArticleEntryAdapter
    private val articleViewModel: ArticleViewModel by viewModels {
        SharedViewModelFactory(sharedViewModel, requireActivity().application)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_article, container, false)

        binding.sharedViewModel = sharedViewModel
        binding.toolbar.title = getString(R.string.article_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            view?.let { it ->
                Navigation.findNavController(it)
                    .navigate(R.id.action_articleFragment_to_orderFragment)
            }
        }
        viewPager = binding.articleEntryViewpager
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userLoginRepository = UserLoginRepository()
        val articleEntryViewModel: ArticleEntryViewModel by viewModels {
            ArticleEntryViewModelFactory(
                sharedViewModel,
                requireActivity().application,
                userLoginRepository
            )
        }
        viewPager = binding.articleEntryViewpager
        viewPagerAdapter = ArticleEntryAdapter(
            childFragmentManager,
            lifecycle,
            //articleViewModel.articles.value ?: emptyList(),
            emptyList(),
            articleEntryViewModel
        )
        viewPager.adapter = viewPagerAdapter

        articleViewModel.articles.observe(viewLifecycleOwner) { articles ->
            //val viewPager2: ViewPager2 = binding.articleEntryViewpager
            //val viewPagerAdapter = ArticleEntryAdapter(childFragmentManager, lifecycle, articles)
            //viewPager2.adapter = viewPagerAdapter
            //viewPagerAdapter = ArticleEntryAdapter(childFragmentManager, lifecycle, articles, articleEntryViewModel)
            //viewPager.adapter = viewPagerAdapter
            viewPagerAdapter.updateData(articles)
            sharedViewModel.setArticleTotal(articles.size)
        }

    }
}