package com.elis.orderingapplication

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.elis.orderingapplication.databinding.FragmentOrderBinding
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.ArticleParcelable
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.OrderParcelable
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
    private val args: OrderFragmentArgs by navArgs()
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
        binding.deliveryAddressName = args.deliveryAddressName
        binding.pointOfServiceName = args.pointOfServiceName
        binding.toolbar.title = getString(R.string.order_selection_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            view?.let { it ->
                Navigation.findNavController(it)
                    .navigate(R.id.action_orderFragment_to_posFragment)
            }
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
                OrdersAdapter(object : OrdersAdapter.MyClickListener {
                    override fun onItemClick(myData: Order) {
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
                                            getOrderDate(order.orderDate), order.appOrderId, orderData
                                        )
                                    )
                                    orderViewModel.onOrderNavigated()
                                    // Updates the order status to STARTED.
                                    orderViewModel.updateOrderStatus(myData)
                                }
                            })

                    }
                })
                        binding . orderSelection . adapter = ordersAdapter
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


    }