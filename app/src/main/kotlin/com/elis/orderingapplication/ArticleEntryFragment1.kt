package com.elis.orderingapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.marginTop
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.adapters.ArticleEntryAdapter
import com.elis.orderingapplication.databinding.FragmentArticleEntryViewpagerBinding
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.OrderEvent
import com.elis.orderingapplication.pojo2.OrderRowsItem
import com.elis.orderingapplication.pojo2.SendOrder
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import com.elis.orderingapplication.viewModels.ArticleEntryViewModel
import com.elis.orderingapplication.viewModels.ArticleEntryViewModelFactory
import com.elis.orderingapplication.viewModels.ArticleViewModel
import com.elis.orderingapplication.viewModels.LoginViewModel
import com.elis.orderingapplication.viewModels.LoginViewModelFactory
import com.elis.orderingapplication.viewModels.ParamsViewModel
import java.util.UUID
import kotlin.math.abs

class ArticleEntryFragment1 : Fragment() {

    private lateinit var binding: FragmentArticleEntryViewpagerBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private val articleViewModel: ArticleViewModel by activityViewModels()
    private lateinit var articleEntryViewModel: ArticleEntryViewModel
    private val args: ArticleFragmentArgs by navArgs()
    private lateinit var recyclerView: RecyclerView
    private var targetQty = ""

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

        val targetQty = arguments?.getString("articleTargetQty")?.toIntOrNull()

        binding.countedQty.doAfterTextChanged {
            val countedQty = binding.countedQty.text.toString().toIntOrNull()
            val resultOrderQty =
                targetQty?.let { targetQty ->
                    countedQty?.let { countedQty ->
                        binding.countedQty.text.toString().toIntOrNull()?.let { countedQty ->
                            targetQty - countedQty
                        } ?: run {
                            targetQty
                        }
                    } ?: run {
                        0
                    }

                    //articles.solOrderQty = "$resultOrderQty".toIntOrNull()
                }
            binding.orderQty.text = "$resultOrderQty"
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.articleEntryRecyclerview
        val order = sharedViewModel.getFilteredOrders()
        val currentOrder = order?.get(0)
        val currentDeliveryAddress = sharedViewModel.deliveryAddressName
        val articles: List<Article>? = order?.flatMap { it.articles!!.toList() }
        val currentArticle = articles?.get(0)
        binding.targetQty.text = currentArticle?.articleTargetQty.toString()

        sharedViewModel.setArticleTotal(articles?.size)

        val rep = UserLoginRepository()
        val provider = ArticleEntryViewModelFactory(rep)

        val articleSendViewModel =
            ViewModelProvider(this, provider)[ArticleEntryViewModel::class.java]

        articleEntryViewModel = articleSendViewModel
        // Sets the data from the arguments of the FragmentManager adapter ArticleEntryAdapter
        val articleNo = arguments?.getString("articleNo")
        val articleDescription = arguments?.getString("articleDescription")
        val articleTargetQty = arguments?.getString("articleTargetQty")
        val articleSize = arguments?.getString("articleSize")
        val numberOfArticles = arguments?.getString("numberOfArticles")
        val currentArticlePosition = arguments?.getString("currentArticlePosition")

        articleEntryViewModel.show(currentArticlePosition, numberOfArticles)

        if (currentArticlePosition == numberOfArticles) {
            binding.lastArticleText.visibility = View.VISIBLE
            binding.sendOrderButton.visibility = View.VISIBLE
        }
        if (arguments != null) {
            binding.articleNo.text = articleNo
            binding.articleDescription.text = articleDescription
            binding.targetQty.text = articleTargetQty
            binding.ofArticlePosition.text = numberOfArticles
            binding.articlePosition.text = currentArticlePosition
        } else
            Toast.makeText(
                requireContext(),
                "There is an issue with data retrieval, please close the app and login again",
                Toast.LENGTH_LONG
            ).show()

        binding.countedQty.doAfterTextChanged {
            val solCountedQty = binding.orderQty.text.toString().toIntOrNull()
            val sendOrderLines = OrderRowsItem(articleSize, solCountedQty, articleNo)
            sharedViewModel.setOrderRowsItem(sendOrderLines)
            //val testing = sharedViewModel.getOrderRowsItem()
        }

        binding.sendOrderButton.setOnClickListener {
            //val sendOrderExternalOrderId = UUID.randomUUID().toString()
            val sendOrderExternalOrderId = abs((0..999999999999999999).random()).toString()
            val sendOrder = SendOrder(
                sharedViewModel.getPosNum().value?.toIntOrNull(),
                sharedViewModel.getDeliveryAddressNo().toIntOrNull(),
                sendOrderExternalOrderId,
                currentOrder?.orderDate,
                sendOrderExternalOrderId,
                sharedViewModel.getOrderRowsItem()
            )
            val sendOrderEvent = OrderEvent(sharedViewModel.getSessionKey(), sendOrder)
            articleEntryViewModel.orderEvent(sendOrderEvent)
            articleEntryViewModel.orderEventResponse.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        val testing = response?.data?.success
                    }

                    is ApiResponse.Loading -> {
                        //orderInfoLoading.visibility = View.VISIBLE
                    }

                    is ApiResponse.Error -> {
                        //orderInfoLoading.visibility = View.INVISIBLE
                        response.data?.let { error ->
                            Log.e("TAG", error.messages.toString())
                            Toast.makeText(
                                requireContext(),
                                error.messages.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            requireActivity().window.clearFlags(
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            )
                        }
                    }

                    is ApiResponse.NoDataError -> {
                        //orderInfoLoading.visibility = View.INVISIBLE
                        val noDataMessage =
                            "There was an issue loading data. Please try again."
                        Log.e("TAG", noDataMessage)

                        Toast.makeText(
                            requireContext(),
                            noDataMessage,
                            Toast.LENGTH_SHORT

                        ).show()
                        requireActivity().window.clearFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        )
                    }

                    else -> {
                        Toast.makeText(
                            requireContext(),
                            "There was an issue loading data. Please try logging in again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        }

    }
}





