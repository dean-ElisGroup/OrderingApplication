package com.elis.orderingapplication

import android.app.AlertDialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.ui.input.key.Key.Companion.Window
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.elis.orderingapplication.adapters.ArticleEntryAdapter
import com.elis.orderingapplication.database.OrderInfoDao
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.databinding.FragmentArticleEntryViewpagerBinding
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.ArticleParcelable
import com.elis.orderingapplication.pojo2.OrderEvent
import com.elis.orderingapplication.pojo2.OrderRowsItem
import com.elis.orderingapplication.pojo2.SendOrder
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import com.elis.orderingapplication.viewModels.ArticleEntryViewModel
import com.elis.orderingapplication.viewModels.ArticleEntryViewModelFactory
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.viewModels.SharedViewModelFactory
import com.elis.orderingapplication.pojo2.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class ArticleEntryCardFragment : Fragment() {

    private var _binding: FragmentArticleEntryViewpagerBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private val articleEntryViewModel: ArticleEntryViewModel by viewModels {
        SharedViewModelFactory(sharedViewModel, requireActivity().application)
    }



    private var currentOrderData: Order? = null
    private var currentArticle: Article? = null
    private var numberOfArticles: Int? = null
    private var currentArticlePosition: Int? = null
    private var currentArticleOrder: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentArticleEntryViewpagerBinding.inflate(inflater, container, false)
        binding.sharedViewModel = sharedViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userLoginRepository = UserLoginRepository()
        val viewModelFactory = ArticleEntryViewModelFactory(
            sharedViewModel,
            requireActivity().application,
            userLoginRepository
        )


    /*view.viewTreeObserver.addOnGlobalFocusChangeListener { oldFocus, newFocus ->
        if (newFocus != null) {
            val viewId = newFocus.id
            val idString = try {
                if (viewId == View.NO_ID) {
                    "NO_ID"
                } else {
                    resources.getResourceEntryName(viewId)
                }
            } catch (e: Resources.NotFoundException) {
                "Unknown ID: $viewId"
            }
            Log.d("FocusLogger", "View ${newFocus.javaClass.simpleName} with ID $idString gained focus")
        }
        if (oldFocus != null) {
            val viewId = oldFocus.id
            val idString = try {
                if (viewId == View.NO_ID) {
                    "NO_ID"
                } else {
                    resources.getResourceEntryName(viewId)
                }
            } catch (e: Resources.NotFoundException) {
                "Unknown ID: $viewId"
            }
            Log.d("FocusLogger", "View ${oldFocus.javaClass.simpleName} with ID $idString lost focus")
        }
    }*/





    ViewModelProvider(this, viewModelFactory)[ArticleEntryViewModel::
    class.java]

    currentArticlePosition = arguments?.getInt("currentArticlePosition")
    currentArticleOrder = arguments?.getInt("currentArticle")

    observeOrderData()
    observeArticleData()
    setupCountedQtyTextChangeListener()
}


private fun observeOrderData() {
    articleEntryViewModel.order.observe(viewLifecycleOwner) { currentOrder ->
        currentOrderData = currentOrder.firstOrNull()
        numberOfArticles = currentOrderData?.totalArticles
        sharedViewModel.setArticleTotal(numberOfArticles ?: 0)
    }
}

private fun observeArticleData() {
    articleEntryViewModel.articles.observe(viewLifecycleOwner) { articles ->
        currentArticle = articles.getOrNull(currentArticleOrder ?: 0)
        updateUI(currentArticle)
    }
    //articleEntryViewModel.articleData.observe(viewLifecycleOwner) { articles ->
    //    currentArticle = articles // articles.getOrNull(currentArticleOrder ?: 0)
    //    updateUI(currentArticle)
   // }
}

private fun updateUI(article: Article?) {
    article?.let {
        binding.articleNo.text = it.articleNo
        binding.articleDescription.text = it.articleDescription
        binding.targetQty.text = it.articleTargetQty.toString()
    }

    val isLastArticle = currentArticlePosition == numberOfArticles
    binding.lastArticleText.isVisible = isLastArticle
    binding.sendOrderButton.isVisible = isLastArticle

    binding.ofArticlePosition.text = numberOfArticles?.toString() ?: ""
    binding.articlePosition.text = currentArticlePosition?.toString() ?: ""
}

private fun setupCountedQtyTextChangeListener() {
    binding.countedQty.doAfterTextChanged {
        val targetQty =
            currentArticle?.articleTargetQty

        fun calculateResultOrderQty(targetQty: Int?, countedQty: Int): Int {
            return targetQty?.let { it - countedQty } ?: 0
        }

        fun updateDatabaseWithResultOrderQty(resultOrderQty: Int, countedQty: Int) {
            currentArticle?.let { article ->
                currentOrderData?.let { orderData ->
                    articleEntryViewModel.updateTextByOtherField(
                        resultOrderQty,
                        countedQty,
                        article.articleNo,
                        orderData.appOrderId
                    )
                }
            }
        }

        val countedQty = binding.countedQty.text.toString().toIntOrNull() ?: 0
        val resultOrderQty = calculateResultOrderQty(targetQty, countedQty)
        binding.orderQty.text = resultOrderQty.toString()
        updateDatabaseWithResultOrderQty(resultOrderQty, countedQty)
        binding.countedQty.requestFocus()
    }

    articleEntryViewModel.uiState.observe(viewLifecycleOwner) { updatedText ->
        binding.orderQty.text = updatedText
        binding.countedQty.requestFocus()
    }
}

override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}
}
/*
    private lateinit var binding: FragmentArticleEntryViewpagerBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private val articleEntryViewModel: ArticleEntryViewModel by viewModels {
        SharedViewModelFactory(sharedViewModel, requireActivity().application)
    }
    private var currentOrderData: Order? = null
    private var currentArticle: Article? = null
    var articleNo: String? = ""
    var articleDescription: String? = ""
    var articleTargetQty: Int? = null
    var articleSize: String? = ""
    private var numberOfArticles: Int? = null
    private var currentArticlePosition: Int? = null
    private var currentArticleOrder: Int? = null


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

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.articleEntryRecyclerview

        val rep = UserLoginRepository()
        val provider =
            ArticleEntryViewModelFactory(sharedViewModel, requireActivity().application, rep)
        val articleSendViewModel =
            ViewModelProvider(this, provider)[ArticleEntryViewModel::class.java]

        currentArticlePosition = arguments?.getInt("currentArticlePosition")
        currentArticleOrder = arguments?.getInt("currentArticle")
        // Observe the Order LiveData from the ViewModel
        articleEntryViewModel.order.observe(viewLifecycleOwner) { currentOrder ->
            currentOrderData = currentOrder.first()
            numberOfArticles = currentOrderData!!.totalArticles
            sharedViewModel.setArticleTotal(numberOfArticles)
        }

        // Observe the Article LiveData from ViewModel
        articleEntryViewModel.articles.observe(viewLifecycleOwner) { article ->
            if (article.isNotEmpty()) {
                currentArticle = article[currentArticleOrder!!]
                articleNo = currentArticle!!.articleNo
                articleDescription = currentArticle!!.articleDescription
                articleTargetQty = currentArticle!!.articleTargetQty
                articleSize = currentArticle!!.articleSize
                articleTargetQty = currentArticle!!.articleTargetQty
            } else {
                showDialog()
            }
            // Check to see if the Last Article text and Send order button should be displayed
            if (currentArticlePosition == numberOfArticles) {
                binding.lastArticleText.visibility = View.VISIBLE
                binding.sendOrderButton.visibility = View.VISIBLE
            }
            if (arguments != null) {
                binding.articleNo.text = articleNo
                binding.articleDescription.text = articleDescription
                binding.targetQty.text = articleTargetQty.toString()
                binding.ofArticlePosition.text = numberOfArticles.toString()
                binding.articlePosition.text = currentArticlePosition.toString()
            } else
                Toast.makeText(
                    requireContext(),
                    "There is an issue with data retrieval, please close the app and login again",
                    Toast.LENGTH_LONG
                ).show()
        }
        binding.countedQty.doAfterTextChanged {
            val targetQty =
                currentArticle?.articleTargetQty // arguments?.getString("articleTargetQty")?.toIntOrNull()

            fun calculateResultOrderQty(targetQty: Int?, countedQty: Int): Int {
                return targetQty?.let { it - countedQty } ?: 0
            }

            fun updateDatabaseWithResultOrderQty(resultOrderQty: Int, countedQty: Int) {
                currentArticle?.let { article ->
                    currentOrderData?.let { orderData ->
                        articleEntryViewModel.updateTextByOtherField(
                            resultOrderQty,
                            countedQty,
                            article.articleNo,
                            orderData.appOrderId
                        )
                    }
                }
            }

            val countedQty = binding.countedQty.text.toString().toIntOrNull() ?: 0
            val resultOrderQty = calculateResultOrderQty(targetQty, countedQty)
            binding.orderQty.text = resultOrderQty.toString()
            updateDatabaseWithResultOrderQty(resultOrderQty, countedQty)
            binding.countedQty.requestFocus()
        }

        articleEntryViewModel.uiState.observe(viewLifecycleOwner) { updatedText ->
            binding.orderQty.text = updatedText
            binding.countedQty.requestFocus()
        }

        binding.sendOrderButton.setOnClickListener {
            //val sendOrderExternalOrderId = UUID.randomUUID().toString()
            val sendOrderExternalOrderId = abs((0..999999999999999999).random()).toString()
            val sendOrder = SendOrder(
                sharedViewModel.getPosNum().value?.toIntOrNull(),
                sharedViewModel.getDeliveryAddressNo().toIntOrNull(),
                sendOrderExternalOrderId,
                //currentOrder?.orderDate,
                currentOrderData?.orderDate,
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

    private fun showDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("No Article Data Found")
        builder.setMessage("Sorry, no Articles are available for selection.")

        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}

 */





