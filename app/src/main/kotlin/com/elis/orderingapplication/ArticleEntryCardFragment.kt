package com.elis.orderingapplication

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.elis.orderingapplication.databinding.FragmentArticleEntryViewpagerBinding
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.OrderEvent
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import com.elis.orderingapplication.viewModels.ArticleEntryViewModel
import com.elis.orderingapplication.viewModels.ArticleEntryViewModelFactory
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.viewModels.SharedViewModelFactory
import com.elis.orderingapplication.pojo2.Order
import java.util.UUID

class ArticleEntryCardFragment : Fragment() {

    private var _binding: FragmentArticleEntryViewpagerBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private val articleEntryViewModel: ArticleEntryViewModel by viewModels {
        SharedViewModelFactory(sharedViewModel, requireActivity().application)
    }
    private lateinit var article: Article
    private var countedQty: Int = 0

    private var currentOrderData: Order? = null
    private var currentArticle: Article? = null
    private var numberOfArticles: Int? = null
    private var currentArticlePosition: Int? = null
    private var currentArticleOrder: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentArticleEntryViewpagerBinding.inflate(inflater, container, false)
        binding.sharedViewModel = sharedViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userLoginRepository = UserLoginRepository()
        val viewModelFactory = ArticleEntryViewModelFactory(
            sharedViewModel, requireActivity().application, userLoginRepository
        )

        ViewModelProvider(this, viewModelFactory)[ArticleEntryViewModel::class.java]

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
            val targetQty = currentArticle?.articleTargetQty

            fun calculateResultOrderQty(
                targetQty: Int?, countedQty: Int, countedIsEmpty: Boolean
            ): Int {
                return targetQty?.let { it - countedQty } ?: 0
            }

            fun updateDatabaseWithResultOrderQty(resultOrderQty: Int, countedQty: Int) {
                currentArticle?.let { article ->
                    currentOrderData?.let { orderData ->
                        articleEntryViewModel.updateTextByOtherField(
                            resultOrderQty, countedQty, article.articleNo, orderData.appOrderId
                        )
                    }
                }
            }

            val countedQtyIsEmpty = binding.countedQty.text.isEmpty()
            countedQty = binding.countedQty.text.toString().toIntOrNull() ?: 0
            val resultOrderQty = calculateResultOrderQty(targetQty, countedQty, countedQtyIsEmpty)
            if (countedQtyIsEmpty) {
                updateDatabaseWithResultOrderQty(0, 0)
                binding.orderQty.text = ""
            } else {
                binding.orderQty.text = resultOrderQty.toString()
                updateDatabaseWithResultOrderQty(resultOrderQty, countedQty)

            }
        }
        binding.sendOrderButton.setOnClickListener {
            currentOrderData?.let { it1 -> sendOrderToSOL(it1) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun externalOrderId(length: Int = 12): String {
        val uuid = UUID.randomUUID()
        val byteArray = ByteArray(16)
        val bytes = uuid.mostSignificantBits.toBigInteger().toByteArray()
        System.arraycopy(bytes, 0, byteArray, 0, 8)
        val bytes2 = uuid.leastSignificantBits.toBigInteger().toByteArray()
        System.arraycopy(bytes2, 0, byteArray, 8, 8)
        val hexString = byteArray.joinToString("") { "%02x".format(it) }
        return hexString.take(length)
    }

    //private val externalSOLOrderId = externalOrderId()
    private fun sendOrderToSOL(order: Order, externalOrderId: String = externalOrderId()) {
        val sendOrder = articleEntryViewModel.sendOrderToSOL(order, externalOrderId)
        val sendOrderEvent = OrderEvent(sharedViewModel.getSessionKey(), sendOrder)
        articleEntryViewModel.orderEvent(sendOrderEvent)
        articleEntryViewModel.orderEventResponse.value = null
        articleEntryViewModel.orderEventResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiResponse.Success -> handleSuccessResponse(response.data?.success)
                is ApiResponse.Loading -> handleLoadingState()
                is ApiResponse.Error -> handleErrorResponse(response.data?.messages)
                is ApiResponse.ErrorSendOrderDate -> handleErrorResponse(response.data?.messages)
                is ApiResponse.NoDataError -> handleNoDataError()
                is ApiResponse.ErrorLogin -> handleUnknownError()
                is ApiResponse.UnknownError -> handleUnknownError()
                else -> handleUnknownError()
            }
        }
    }

    private fun handleSuccessResponse(success: Boolean?) {
        currentOrderData?.let { articleEntryViewModel.updateOrderStatus(it) }

        if (success == true) {
            Toast.makeText(requireContext(), "Order sent to Sol", Toast.LENGTH_SHORT).show()
        }
    }
    private fun handleLoadingState() {
        // Show loading indicator
    }

    private fun handleErrorResponse(errorMessages: List<String?>?) {
        errorMessages?.let { messages ->
            Log.e("TAG", messages.toString())

            // Create an AlertDialog.Builder
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Error")
                .setMessage(messages.joinToString("\n"))
                .setPositiveButton("OK") { _, _ ->
                    // Handle OK button click if needed
                    clearNotTouchableFlag()
                }
                .setCancelable(false) // Prevent dismissing the dialog by tapping outside
                .show()

            clearNotTouchableFlag()
        }
    }

    private fun handleNoDataError() {
        val noDataMessage = "There was an issue loading data. Please try again."
        Log.e("TAG", noDataMessage)

        // Create an AlertDialog.Builder
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
            .setMessage(noDataMessage)
            .setPositiveButton("OK") { _, _ ->
                // Handle OK button click if needed
                clearNotTouchableFlag()
            }
            .setCancelable(false) // Prevent dismissing the dialog by tapping outside
            .show()

        clearNotTouchableFlag()
    }

    private fun handleUnknownError() {
        val noDataMessage = "There was an unkown error. Please try again.1"
        Log.e("TAG", noDataMessage)

        // Create an AlertDialog.Builder
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Login Error")
            .setMessage(noDataMessage)
            .setPositiveButton("OK") { _, _ ->
                // Handle OK button click if needed
                clearNotTouchableFlag()
            }
            .setCancelable(false) // Prevent dismissing the dialog by tapping outside
            .show()

        clearNotTouchableFlag()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun clearNotTouchableFlag() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}





