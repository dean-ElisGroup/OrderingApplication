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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.elis.orderingapplication.constants.Constants
import com.elis.orderingapplication.databinding.FragmentArticleEntryViewpagerBinding
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.OrderEvent
import com.elis.orderingapplication.repositories.AppRepository
import com.elis.orderingapplication.utils.ApiResponse
import com.elis.orderingapplication.viewModels.ArticleEntryViewModel
import com.elis.orderingapplication.viewModels.AppViewModelFactory
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.viewModels.SharedViewModelFactory
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.OrderEventResponse
import com.elis.orderingapplication.utils.NetworkUtils
import com.gu.toolargetool.TooLargeTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import java.time.Duration
import java.util.UUID

class ArticleEntryCardFragment : Fragment() {

    private var _binding: FragmentArticleEntryViewpagerBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private val articleEntryViewModel: ArticleEntryViewModel by viewModels {
        SharedViewModelFactory(sharedViewModel, requireActivity().application)
    }
    private var countedQty: Int = 0
    private var currentOrderData: Order? = null
    private var currentArticle: Article? = null
    private var numberOfArticles: Int? = null
    private var totalArticles: Int? = null
    private var currentArticlePosition: Int? = null
    private var currentArticleOrder: Int? = null
    private var isNavigationInProgress = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentArticleEntryViewpagerBinding.inflate(inflater, container, false)
        binding.sharedViewModel = sharedViewModel

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appRepository = AppRepository()
        val viewModelFactory = AppViewModelFactory(
            sharedViewModel, requireActivity().application, appRepository
        )

        ViewModelProvider(this, viewModelFactory)[ArticleEntryViewModel::class.java]

        currentArticlePosition = arguments?.getInt("currentArticlePosition")
        currentArticleOrder = arguments?.getInt("currentArticle")
        totalArticles = arguments?.getInt("totalArticles")
        observeOrderData()
        observeArticleData()
        setupCountedQtyTextChangeListener()
        TooLargeTool.startLogging(requireActivity().application)
    }

    private fun observeOrderData() {
        articleEntryViewModel.order.observe(viewLifecycleOwner) { currentOrder ->
            currentOrderData = currentOrder.firstOrNull()
            numberOfArticles = currentOrderData?.totalArticles
            sharedViewModel.setArticleTotal(numberOfArticles ?: 0)
            val orderStatusCallback = sharedViewModel.getOrderStatusCallback()
            orderStatusCallback?.onOrderStatusDataReceived(currentOrderData)

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
            _binding?.article = article
        }

        val isLastArticle = currentArticlePosition == totalArticles //numberOfArticles
        binding.lastArticleText.isVisible = isLastArticle
        val lastArticleCallback = sharedViewModel.getLastArticleCallback()

        lastArticleCallback?.onLastArticleChanged(isLastArticle)


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
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                startInternetCheckJob()
                //currentOrderData?.let { it1 -> sendOrderToSOL(it1) }
            }
        }
    }

    fun startInternetCheckJob(): Job {
        return viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                val isInternetAvailable = NetworkUtils.isInternetAvailable(requireContext())
                if (isInternetAvailable && currentOrderData != null) {
                    // Internet is available, perform your desired actions
                    sendOrderToSOL(currentOrderData!!)
                    break
                } else {
                    // Internet is not available
                    currentOrderData?.let {
                        articleEntryViewModel.updateOrderStatus(
                            it,
                            Constants.APP_STATUS_FINISHED.toString(),
                            Constants.ORDER_STATUS_FINISHED
                        )
                    }
                    orderNotSubmittedDialog()
                }
                delay(Duration.ofSeconds(5)) // Delay for 5 seconds before checking again
            }
        }
    }

    private fun externalOrderId(length: Int = 12): String {
        return UUID.randomUUID().toString().replace("-", "").take(length)
    }

    private fun sendOrderToSOL(order: Order, externalOrderId: String = externalOrderId()) {
        val sendOrder = articleEntryViewModel.sendOrderToSOL(order, externalOrderId)
        val sendOrderEvent = OrderEvent(sharedViewModel.getSessionKey(), sendOrder)

        viewLifecycleOwner.lifecycleScope.launch {
            val response = articleEntryViewModel.orderEvent(sendOrderEvent)
            handleResponse(response) // No need for view check here
        }
    }

    private suspend fun handleResponse(response: ApiResponse<OrderEventResponse>?) {
        if (isAdded && view != null) { // Check view availability before handling response
            response?.let { apiResponse ->
                when (apiResponse) {
                    is ApiResponse.Success<*> -> {
                        val successResponse = apiResponse as ApiResponse.Success<OrderEventResponse>
                        handleSuccessResponse(successResponse.data?.success)
                    }

                    is ApiResponse.Loading<*> -> handleLoadingState()
                    is ApiResponse.Error<*> -> handleErrorResponse(apiResponse.message)
                    is ApiResponse.ErrorSendOrderDate<*> -> handleErrorResponse(apiResponse.message)
                    is ApiResponse.NoDataError<*> -> handleNoDataError()
                    is ApiResponse.ErrorLogin<*> -> handleUnknownError()
                    is ApiResponse.UnknownError<*> -> handleUnknownError()
                    else -> handleUnknownError()
                }
            }
        }
    }

    private fun handleSuccessResponse(success: Boolean?) {
        currentOrderData?.let {
            articleEntryViewModel.updateOrderStatus(
                it, Constants.APP_STATUS_SENT.toString(),
                Constants.ORDER_STATUS_FINISHED
            )
        }
        if (success == true && isResumed) { // isVisible) {
            //Toast.makeText(requireContext(), "Order sent to Sol", Toast.LENGTH_SHORT).show()

            if (findNavController().currentDestination?.id != R.id.orderFragment) {
                findNavController().navigate(R.id.action_articleFragment_to_orderFragment)
                Toast.makeText(requireContext(), "Order sent to Sol", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(requireContext(), "Order sent to Sol", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleLoadingState() {
        // Show loading indicator
    }

    private fun handleErrorResponse(errorMessages: String?) {
        errorMessages?.let { messages ->
            Log.e("ERROR_MESSAGE", messages.toString())

            // Create an AlertDialog.Builder
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Error")
                .setMessage(messages.replace(Regex("\\[|\\]"), ""))
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
        val noDataMessage = "There was an error. Please try again."
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

    private fun clearNotTouchableFlag() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun onResume() {
        super.onResume()
        updateUI(currentArticle)
    }

    private fun orderNotSubmittedDialog() {
        if (!isNavigationInProgress) {
            isNavigationInProgress =
                true // Set the flag to true to indicate that a navigation is in progress
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Order Not Submitted")
                .setMessage("There is currently no connection to the internet.\nThe order has not been sent to SOL.")
                .setPositiveButton("OK") { _, _ ->
                    // No need to navigate since you're already on the orderFragment
                    findNavController().navigate((R.id.action_articleFragment_to_orderFragment))
                    clearNotTouchableFlag()
                    isNavigationInProgress =
                        false // Reset the flag after the navigation is processed
                }
                .setCancelable(false).show() // Prevent dismissing the dialog by tapping outside
        } else {
            // Navigation is already in progress, so a toast is shown to the user
            Toast.makeText(
                requireContext(),
                "Navigation already in progress",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    interface LastArticleCallback {
        fun onLastArticleChanged(isLastArticle: Boolean)
    }

    interface OrderStatusCallback {
        fun onOrderStatusDataReceived(orderData: Order?)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference
    }
}





