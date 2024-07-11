package com.elis.orderingapplication

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.compose.ui.unit.Constraints
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.adapters.listAdapters.OrdersAdapter
import com.elis.orderingapplication.constants.Constants
import com.elis.orderingapplication.databinding.FragmentOrderBinding
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.ArticleParcelable
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.OrderParcelable
import com.elis.orderingapplication.utils.DeviceInfo
import com.elis.orderingapplication.utils.DeviceInfoDialog
import com.elis.orderingapplication.viewModels.OrderViewModel
import com.elis.orderingapplication.viewModels.SharedViewModelFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class OrderFragment : Fragment() {

    private lateinit var binding: FragmentOrderBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var ordersAdapter: OrdersAdapter
    private val orderViewModel: OrderViewModel by viewModels {
        SharedViewModelFactory(sharedViewModel, requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false)

        binding.sharedViewModel = sharedViewModel
        binding.orderViewModel = orderViewModel
        binding.toolbar.title = getString(R.string.order_selection_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setTitleTextAppearance(requireContext(),R.style.titleTextStyle)
        binding.toolbar.setNavigationOnClickListener {
            view?.let { it ->
                Navigation.findNavController(it)
                    .navigate(R.id.action_orderFragment_to_posFragment)
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
                    findNavController().navigate(R.id.action_orderFragment_to_landingPageFragment)
                    true
                }

                else -> false
            }
        }
        observeArgsBundleFromTest()
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFlavorBanner()
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
                orderData.appOrderId
            )

            return orderParcelable
        }

        ordersAdapter =
            OrdersAdapter(object : OrdersAdapter.MyClickListener {
                override fun onItemClick(
                    myData: Order,
                    isClickable: Boolean,
                    orderStatus: String?
                ) {
                    if (!isClickable) {
                        orderDialog(orderStatus)
                    } else {
                        orderViewModel.onOrderClicked(myData)
                        sharedViewModel.setArticleDeliveryDate(myData.orderDate.toString())
                        sharedViewModel.setArticleAppOrderId(myData.appOrderId)
                        val orderData = orderToParcelable(myData)
                        orderViewModel.navigateToOrder.observe(
                            viewLifecycleOwner,
                            Observer { order ->
                                order?.let {
                                    findNavController().navigate(
                                        OrderFragmentDirections.actionOrderFragmentToArticleFragment(
                                            getOrderDate(order.orderDate),
                                            order.appOrderId,
                                            orderData
                                        )
                                    )
                                    orderViewModel.onOrderNavigated()
                                    // Updates the order status to STARTED.
                                    orderViewModel.updateOrderStatus(myData)
                                }
                            })
                    }
                }
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
        builder.setTitle("No Data Found")
        builder.setMessage("Sorry, no orders are available for selection.")

        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            findNavController().navigate(R.id.action_orderFragment_to_posFragment)
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun setFlavorBanner() {
        // sets banner text
        if (sharedViewModel.flavor.value == "development") {
            binding.debugBanner.visibility = View.VISIBLE
            binding.bannerText.visibility = View.VISIBLE
            binding.debugBanner.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_200
                )
            )
            binding.bannerText.text = resources.getString(R.string.devFlavorText)
        }
        // hides banner if PROD application
        if (sharedViewModel.flavor.value == "production") {
            binding.debugBanner.visibility = View.GONE
            binding.bannerText.visibility = View.GONE
        }
        // sets banner text and banner color
        if (sharedViewModel.flavor.value == "staging") {
            binding.debugBanner.visibility = View.VISIBLE
            binding.bannerText.visibility = View.VISIBLE
            binding.debugBanner.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.elis_orange
                )
            )
            binding.bannerText.text = resources.getString(R.string.testFlavorText)
        }
    }

    private fun observeArgsBundleFromTest() {
        sharedViewModel.argsBundleFromTest.observe(viewLifecycleOwner) { bundle ->
            val deliveryAddressName = bundle.getString("DELIVERY_ADDRESS_NAME", "")
            //val orderingGroupName = bundle.getString("ORDERING_GROUP", "")
            val pointOfServiceName = bundle.getString("POINT_OF_SERVICE_NAME", "")
            binding.deliveryAddressName = deliveryAddressName
            binding.pointOfServiceName = pointOfServiceName
        }
    }

    private fun orderDialog(orderStatus: String?) {
        var message = ""
        if (orderStatus == Constants.APP_STATUS_FINISHED.toString()) {
            message =
                "Order has been finished. Please submit the order using the Send Order option."
        } else if (orderStatus == Constants.APP_STATUS_SENT.toString()) {
            message =
                "Order has already been submitted."
        }


        val dialog = AlertDialog.Builder(requireContext())
            .setIcon(R.drawable.outline_error_24)
            .setTitle("Order")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create()
        dialog.show()
    }


}