package com.elis.orderingapplication.sendOrder

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.CardViewDecoration
import com.elis.orderingapplication.R
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.adapters.listAdapters.SendOrdersAdapter
import com.elis.orderingapplication.constants.Constants.Companion.SHOW_BANNER
import com.elis.orderingapplication.databinding.FragmentSendOrderOrderBinding
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.OrderEvent
import com.elis.orderingapplication.pojo2.OrderParcelable
import com.elis.orderingapplication.repositories.AppRepository
import com.elis.orderingapplication.utils.ApiResponse
import com.elis.orderingapplication.utils.DeviceInfo
import com.elis.orderingapplication.utils.DeviceInfoDialog
import com.elis.orderingapplication.utils.FlavorBannerUtils
import com.elis.orderingapplication.utils.NetworkUtils
import com.elis.orderingapplication.viewModels.AppViewModelFactory
import com.elis.orderingapplication.viewModels.SharedViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class SendOrderFragment : Fragment() {

    private var _binding: FragmentSendOrderOrderBinding? = null

    private val binding: FragmentSendOrderOrderBinding get() = _binding!!
    private val sharedViewModel: ParamsViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var ordersAdapter: SendOrdersAdapter
    private var deliveryAddressForArgs: String = ""
    lateinit var orderData: OrderParcelable
    private var isSendOrderInProgress = false
    private var isSendOrderDialogShown = false

    private val orderViewModel: SendOrderViewModel by viewModels {
        SharedViewModelFactory(sharedViewModel, requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_send_order_order, container, false)

        binding.sharedViewModel = sharedViewModel
        binding.toolbar.title = getString(R.string.send_order_order_screen)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setTitleTextAppearance(requireContext(), R.style.titleTextStyle)
        binding.toolbar.setNavigationOnClickListener {
            view?.let {
                Navigation.findNavController(it)
                    .navigate(R.id.action_sendOrderOrderFragment_to_sendDeliveryAddressFragment)
            }
        }
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.overflow -> {
                    val deviceInfo = DeviceInfo(requireContext())
                    DeviceInfoDialog.showAlertDialog(requireContext(), deviceInfo.getDeviceInfo())
                    true
                }

                R.id.home_button -> {
                    findNavController().navigate(R.id.action_sendOrderOrderFragment_to_landingPageFragment)
                    true
                }

                else -> false
            }
        }

        val deliveryAddressFromArgs =
            sharedViewModel.argsBundleFromTest.value?.getString("DELIVERY_ADDRESS_NAME", "")
        if (deliveryAddressFromArgs != null) {
            binding.deliveryAddressName = deliveryAddressFromArgs
        } else {
            sharedViewModel.argsBundleFromTest.observe(viewLifecycleOwner, Observer {
                deliveryAddressForArgs = it.getString("DELIVERY_ADDRESS_NAME", "")
                binding.deliveryAddressName = deliveryAddressForArgs
            })
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (SHOW_BANNER) {
            FlavorBannerUtils.setupFlavorBanner(
                resources,
                requireContext(),
                binding,
                sharedViewModel
            )
            binding.debugBanner.visibility = VISIBLE
        }
        recyclerView = binding.orderSelection
        binding.orderSelection.isEnabled = !isSendOrderDialogShown

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        val itemSpacingDecoration = CardViewDecoration(spacingInPixels)
        recyclerView.addItemDecoration(itemSpacingDecoration)

        fun orderToParcelable(orderData: Order): OrderParcelable {
            val orderParcelable = OrderParcelable(
                orderData.orderType,
                orderData.orderDate,
                orderData.deliveryDate,
                orderData.orderStatus,
                orderData.appOrderStatus,
                orderData.appPosNo,
                orderData.posName,
                orderData.totalArticles,
                orderData.deliveryAddressNo,
                orderData.deliveryAddressName,
                orderData.appOrderId
            )

            return orderParcelable
        }

        val appRepository = AppRepository()
        val viewModelFactory = AppViewModelFactory(
            sharedViewModel, requireActivity().application, appRepository
        )

        ViewModelProvider(this, viewModelFactory)[SendOrderViewModel::class.java]

        ordersAdapter =
            SendOrdersAdapter(object : SendOrdersAdapter.MyClickListener {
                override fun onItemClick(myData: Order) {
                    if (!isSendOrderDialogShown) {
                        orderViewModel.onOrderClicked(myData)
                        sharedViewModel.setArticleDeliveryDate(myData.orderDate.toString())
                        sharedViewModel.setArticleAppOrderId(myData.appOrderId)
                        //orderData = orderToParcelable(myData)
                    }
                }
            })

        orderViewModel.navigateToOrder.observe(
            viewLifecycleOwner,
            Observer { order ->
                sendOrderDialog(order)
            })


        binding.orderSelection.adapter = ordersAdapter
        // Observe the LiveData from the ViewModel
        orderViewModel.orders.observe(viewLifecycleOwner) { orders ->
            ordersAdapter.setData(orders)
            if (orders.isEmpty()) {
                showDialog()
            }
        }
    }

    fun getOrderDate(orderDate: String?): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.getDefault())
        val date = LocalDate.parse(orderDate, inputFormatter)

        return outputFormatter.format(date)
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("No Orders Found")
        builder.setMessage("Sorry, no orders are available for sending.")

        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            findNavController().navigate(R.id.action_sendOrderOrderFragment_to_sendDeliveryAddressFragment)
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun sendOrderDialog(order: Order?) {
        val isInternetAvailable = NetworkUtils.isInternetAvailable(requireContext())
        if (isInternetAvailable) {
            isSendOrderDialogShown = true
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Send order")
            builder.setMessage("${order?.posName}\n\nAre you sure you wish to send this order to SOL?")

            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                if (order != null) {
                    sendOrderToSOL(order, externalOrderId())
                }
                clearNotTouchableFlag()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                isSendOrderInProgress = false
                binding.orderSelection.isEnabled = true
                clearNotTouchableFlag()
            }
            val dialog = builder.create()
            dialog.setOnDismissListener {
                isSendOrderDialogShown = false // Re-enable the RecyclerView
                binding.orderSelection.isEnabled = true
                clearNotTouchableFlag()
            }
            isSendOrderDialogShown = true // Disable the RecyclerView
            binding.orderSelection.isEnabled = false
            requireActivity().window.addFlags((WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE))
            dialog.setCancelable(false)

            dialog.show()
        } else {
            showNoConnectionDialog()
        }
    }

    private fun showNoConnectionDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Internet Connection")
        builder.setMessage("There is currently no internet connection. Please check your connection and try again.")
        builder.setIcon(R.drawable.outline_error_24)
        builder.setPositiveButton("OK") { dialog, which ->
            // Handle positive button click
            dialog.dismiss()
        }

        builder.setCancelable(true)

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
    }

    /*private fun externalOrderId(length: Int = 12): String {
        val uuid = UUID.randomUUID()
        val byteArray = ByteArray(16)
        val bytes = uuid.mostSignificantBits.toBigInteger().toByteArray()
        System.arraycopy(bytes, 0, byteArray, 0, 8)
        val bytes2 = uuid.leastSignificantBits.toBigInteger().toByteArray()
        System.arraycopy(bytes2, 0, byteArray, 8, 8)
        val hexString = byteArray.joinToString("") { "%02x".format(it) }
        return hexString.take(length)
    }*/

    private fun externalOrderId(length: Int = 12): String {
        return UUID.randomUUID().toString().replace("-", "").take(length)
    }

    private fun sendOrderToSOL(order: Order, externalOrderId: String) {
        val sendOrder = orderViewModel.sendOrderToSOL(order, externalOrderId)
        val sendOrderEvent = OrderEvent(sharedViewModel.getSessionKey(), sendOrder)
        orderViewModel.orderEvent(sendOrderEvent)
        orderViewModel.orderEventResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                when (response) {
                    is ApiResponse.Success -> handleSuccessResponse(response.data?.success, order)
                    is ApiResponse.Loading -> handleLoadingState()
                    is ApiResponse.Error -> handleErrorResponse(response.message)
                    is ApiResponse.ErrorSendOrderDate -> handleErrorResponse(response.message)
                    is ApiResponse.NoDataError -> handleNoDataError()
                    is ApiResponse.ErrorLogin -> handleUnknownError()
                    is ApiResponse.UnknownError -> handleUnknownError()
                    else -> handleUnknownError()
                }
            }
        }
    }

    private fun handleSuccessResponse(success: Boolean?, order: Order?) {
        order?.let { orderViewModel.updateOrderStatus(it) }
        if (success == true) {
            Toast.makeText(
                requireContext(),
                "Order has been successfully submitted.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleLoadingState() {
        // Show loading indicator
    }

    private fun handleErrorResponse(errorMessages: String?) {
        errorMessages?.let { messages ->
            Log.e("ERROR_MESSAGE", messages)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference
    }
}