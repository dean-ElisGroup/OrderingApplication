package com.elis.orderingapplication.sendOrder

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.CardViewDecoration
import com.elis.orderingapplication.R
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.adapters.listAdapters.OrdersAdapter
import com.elis.orderingapplication.adapters.listAdapters.SendOrdersAdapter
import com.elis.orderingapplication.databinding.FragmentSendOrderOrderBinding
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.OrderEvent
import com.elis.orderingapplication.pojo2.OrderParcelable
import com.elis.orderingapplication.utils.ApiResponse
import com.elis.orderingapplication.viewModels.SharedViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class SendOrderFragment : Fragment() {

    private lateinit var binding: FragmentSendOrderOrderBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private val args: SendOrderFragmentArgs by navArgs()
    private lateinit var recyclerView: RecyclerView
    private lateinit var ordersAdapter: SendOrdersAdapter
    private var deliveryAddressForArgs: String = ""
    private val orderViewModel: SendOrderViewModel by viewModels {
        SharedViewModelFactory(sharedViewModel, requireActivity().application)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_send_order_order, container, false)

        binding.sharedViewModel = sharedViewModel
        binding.orderViewModel = orderViewModel
        //binding.deliveryAddressName = args.deliveryAddressName
        binding.toolbar.title = getString(R.string.order_selection_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            view?.let { it ->
                Navigation.findNavController(it)
                    .navigate(R.id.action_sendOrderOrderFragment_to_sendDeliveryAddressFragment)
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

        recyclerView = binding.orderSelection

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
                orderData.appOrderId)

            return orderParcelable
        }
                ordersAdapter =
                SendOrdersAdapter(object : SendOrdersAdapter.MyClickListener {
                    override fun onItemClick(myData: Order) {
                        orderViewModel.onOrderClicked(myData)
                        sharedViewModel.setArticleDeliveryDate(myData.orderDate.toString())
                        sharedViewModel.setArticleAppOrderId(myData.appOrderId)
                        val orderData = orderToParcelable(myData)
                    }
                })

        orderViewModel.navigateToOrder.observe(
            viewLifecycleOwner,
            Observer { order ->
                sendOrderDialog(order)
                //Toast.makeText(activity, order?.appOrderId, Toast.LENGTH_LONG).show()
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
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Send order")
        builder.setMessage("${order?.posName}\n\nAre you sure you wish to send this order to SOL?")

        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            //findNavController().navigate(R.id.action_sendOrderOrderFragment_to_sendDeliveryAddressFragment)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun sendOrderToSOL(order: Order, externalOrderId: String = externalOrderId()) {
        val sendOrder = orderViewModel.sendOrderToSOL(order, externalOrderId)
        val sendOrderEvent = OrderEvent(sharedViewModel.getSessionKey(), sendOrder)
        orderViewModel.orderEvent(sendOrderEvent)
        orderViewModel.orderEventResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                when (response) {
                    is ApiResponse.Success -> handleSuccessResponse(response.data?.success)
                    is ApiResponse.Loading -> handleLoadingState()
                    is ApiResponse.Error -> handleErrorResponse(response.message)// .data?.messages)
                    is ApiResponse.ErrorSendOrderDate -> handleErrorResponse(response.message)
                    is ApiResponse.NoDataError -> handleNoDataError()
                    is ApiResponse.ErrorLogin -> handleUnknownError()
                    is ApiResponse.UnknownError -> handleUnknownError()
                    else -> handleUnknownError()
                }
            }
        }
    }

    private fun handleSuccessResponse(success: Boolean?) {
        currentOrderData?.let { orderViewModel.updateOrderStatus(it) }

        if (success == true) {
            Toast.makeText(requireContext(), "Order sent to Sol", Toast.LENGTH_SHORT).show()
        }
    }


    }