package com.elis.orderingapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.adapters.OrderAdapter
import com.elis.orderingapplication.databinding.FragmentOrderBinding
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.viewModels.OrderViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrderFragment : Fragment() {

    private lateinit var binding: FragmentOrderBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private val orderViewModel: OrderViewModel by activityViewModels()
    private val args: OrderFragmentArgs by navArgs()
    private lateinit var recyclerView: RecyclerView


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
        binding.toolbar.setNavigationOnClickListener {
            view?.let { it ->
                Navigation.findNavController(it)
                    .navigate(R.id.action_orderFragment_to_posFragment)
            }
        }
        // Sets ordering group and ordering name to shared ViewModel
        //sharedViewModel.setOrderingGroupNo(args.orderingGroupNo)
        //sharedViewModel.setOrderingGroupName(args.orderingGroupName)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.orderSelection

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        val itemSpacingDecoration = CardViewDecoration(spacingInPixels)
        recyclerView.addItemDecoration(itemSpacingDecoration)

        val posList =
            sharedViewModel.getFilteredPointsOfService()?.filter { it.pointOfServiceNo == args.pos }

        val orders: List<Order>? = posList?.flatMap { it.orders!!.toList() }
        val filteredOrders =
            orders?.filter { it.orderDate == getOrderDate() && it.orderType == "inventory" }
                ?.toMutableList()
        // Sets filtered order data to global shared view model.
        sharedViewModel.setFilteredOrders(filteredOrders)

        val articles: List<Article>? = filteredOrders?.flatMap { it.articles!!.toMutableList() }
        val totalArticles = articles?.size

        val iterator = filteredOrders?.listIterator()
        while(iterator!!.hasNext()) {
            val i = iterator.next()
            i.totalArticles = totalArticles
        }


        //sharedViewModel.setArticleTotal(testing)
        //articles?.size?.let { sharedViewModel.setArticleTotal(it) }

        val adapter =
            OrderAdapter(OrderAdapter.OrderListener { order ->
                orderViewModel.onOrderClicked(order)
                orderViewModel.navigateToOrder.observe(
                    viewLifecycleOwner,
                    Observer { order ->
                        order?.let {
                            this.findNavController().navigate(
                                OrderFragmentDirections.actionOrderFragmentToArticleFragment(order.orderDate)
                            )
                            orderViewModel.onOrderNavigated()
                        }
                    })
            })
        adapter.submitList(filteredOrders)

        binding.orderSelection.adapter = adapter
        recyclerView.adapter = adapter

    }

    fun getOrderDate(): String? {
        var orderDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDateTime.now().format(orderDateFormatter)
    }


}