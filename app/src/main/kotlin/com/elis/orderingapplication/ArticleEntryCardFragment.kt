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
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import com.elis.orderingapplication.viewModels.ArticleEntryViewModel
import com.elis.orderingapplication.viewModels.ArticleEntryViewModelFactory
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.viewModels.SharedViewModelFactory
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.OrderEventResponse
import com.elis.orderingapplication.utils.NetworkUtils
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
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
    private lateinit var article: Article
    private var countedQty: Int = 0

    private var currentOrderData: Order? = null
    private var currentArticle: Article? = null
    private var numberOfArticles: Int? = null
    private var totalArticles: Int? = null
    private var currentArticlePosition: Int? = null
    private var currentArticleOrder: Int? = null

    private var bindingEntry: FragmentArticleEntryViewpagerBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentArticleEntryViewpagerBinding.inflate(inflater, container, false)
        binding.sharedViewModel = sharedViewModel


        return _binding!!.root // binding.root
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
        totalArticles = arguments?.getInt("totalArticles")
        observeOrderData()
        observeArticleData()
        setupCountedQtyTextChangeListener()
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
// Starts a check to see if there's a physical connection to the internet.
    fun startInternetCheckJob() {
        lifecycleScope.launch {
            while (isActive) {
                val isInternetAvailable = NetworkUtils.isInternetAvailable(requireContext())
                if (isInternetAvailable) {
                    // Internet is available, perform your desired actions
                    currentOrderData?.let { it1 -> sendOrderToSOL(it1) }
                    // navigate back to the orders screen
                    findNavController().navigate(R.id.action_articleFragment_to_orderFragment)
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
                delay(Duration.ofSeconds(5000)) // Delay for 5 seconds before checking again
            }
        }
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

    private fun sendOrderToSOL(order: Order, externalOrderId: String = externalOrderId()) {
        val sendOrder = articleEntryViewModel.sendOrderToSOL(order, externalOrderId)
        val sendOrderEvent = OrderEvent(sharedViewModel.getSessionKey(), sendOrder)

        viewLifecycleOwner.lifecycleScope.launch {
            val response = articleEntryViewModel.orderEvent(sendOrderEvent)
            handleResponse(response)
        }
    }

    private suspend fun handleResponse(response: ApiResponse<OrderEventResponse>?) {
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

    private fun handleSuccessResponse(success: Boolean?) {
        currentOrderData?.let {
            articleEntryViewModel.updateOrderStatus(
                it, Constants.APP_STATUS_SENT.toString(),
                Constants.ORDER_STATUS_FINISHED
            )

        }

        if (success == true) {
            Toast.makeText(requireContext(), "Order sent to Sol", Toast.LENGTH_SHORT).show()
            // Creates a document in the Firestore database using the data in the Order object
            /*
            val db = Firebase.firestore
            val orderData = hashMapOf(
                "delvDate" to currentOrderData?.deliveryDate.toString(),
                "orderDate" to currentOrderData?.orderDate.toString(),
                "orderId" to currentOrderData?.appOrderId)

            db.collection("elis_orders").add(orderData).addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
            }.addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }

             */
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
        val noDataMessage = "There was an unknown error. Please try again."
        Log.e("TAG", noDataMessage)

        // Create an AlertDialog.Builder
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Unknown Error")
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

    override fun onResume() {
        super.onResume()
            updateUI(currentArticle)
    }

    private fun orderNotSubmittedDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Order Not Submitted")
            .setMessage("There is currently no connection to the internet.\nThe order has not been sent to SOL.")
            .setPositiveButton("OK") { _, _ ->
                // Handle OK button click if needed
                findNavController().navigate(R.id.action_articleFragment_to_orderFragment)
                clearNotTouchableFlag()
            }
            .setCancelable(false).show() // Prevent dismissing the
    }

    interface LastArticleCallback {
        fun onLastArticleChanged(isLastArticle: Boolean)
    }

    interface OrderStatusCallback {
        fun onOrderStatusDataReceived(orderData: Order?)
    }
}





