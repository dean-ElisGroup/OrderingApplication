package com.elis.orderingapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.adapters.DeliveryAdapter
//import com.elis.orderingapplication.adapters.OrderingGroupAdapter
import com.elis.orderingapplication.databinding.FragmentPosGroupBinding
import com.elis.orderingapplication.pojo2.PointsOfService
import com.elis.orderingapplication.viewModels.DeliveryAddressViewModel
import com.elis.orderingapplication.viewModels.OrderingGroupViewModel
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.google.common.collect.Ordering
import com.elis.orderingapplication.adapters.listAdapters.OrderingGroupAdapter
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.JoinOrderingGroup

class PosGroupFragment : Fragment() {

    private lateinit var binding: FragmentPosGroupBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    //private val orderingGroupViewModel: OrderingGroupViewModel by activityViewModels()
    private val args: PosGroupFragmentArgs by navArgs()
    private lateinit var orderingGroupAdapter: OrderingGroupAdapter
    private val orderingGroupViewModel: OrderingGroupViewModel by lazy {
        ViewModelProvider(this)[OrderingGroupViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPosGroupBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.orderingGroupViewModel = orderingGroupViewModel
        binding.sharedViewModel = sharedViewModel

        binding.toolbar.title = getString(R.string.pos_group_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            view?.let { it ->
                findNavController(it)
                    .navigate(R.id.action_posGroupFragment_to_deliveryAddressFragment)
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        sharedViewModel.setDeliveryAddressName(args.deliveryAddressNo)

        if (args.deliveryAddressNo.isNullOrEmpty())
            binding.deliveryAddressName = sharedViewModel.deliveryAddressName.value
        else
            binding.deliveryAddressName = args.deliveryAddressNo

        val recyclerView: RecyclerView = binding.orderingGroupSelection
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        val itemSpacingDecoration = CardViewDecoration(spacingInPixels)
        recyclerView.addItemDecoration(itemSpacingDecoration)
        // Finds delivery address data based on the selected delivery address
        val filteredOrderInfo = sharedViewModel.getDeliveryAddresses()
            ?.filter { it.deliveryAddressNo == args.deliveryAddressNo }
        // Sets current selected delivery address name
        filteredOrderInfo?.get(0)?.deliveryAddressName?.let {
            sharedViewModel.setDeliveryAddressName(
                it
            )
        }
        // Observe the LiveData from the ViewModel
        //orderingGroupViewModel.orderingGroup.observe(viewLifecycleOwner) { joinOrderingGroup ->
        //    val test5 = joinOrderingGroup

        //}

        orderingGroupAdapter = OrderingGroupAdapter(object : OrderingGroupAdapter.MyClickListener {
            override fun onItemClick(myData: JoinOrderingGroup) {
                orderingGroupViewModel.onOrderingGroupClicked(myData)
                orderingGroupViewModel.navigateToOrderingGroup.observe(
                    viewLifecycleOwner,
                    Observer { orderingGroup ->
                        orderingGroup?.let {
                            findNavController().navigate(
                                PosGroupFragmentDirections.actionPosGroupFragmentToPosFragment(
                                    orderingGroup.orderingGroupNo, orderingGroup.orderingGroupDescription
                                )
                            )
                            orderingGroupViewModel.onOrderingGroupNavigated()
                        }
                    })

            }
        })

        binding.orderingGroupSelection .adapter = orderingGroupAdapter

        // Observe the LiveData from the ViewModel
        orderingGroupViewModel.orderingGroup.observe(viewLifecycleOwner) { orderingGroup ->
            orderingGroupAdapter.setData(orderingGroup)
        }

/*
        // Sets OrderingGroups data
        sharedViewModel.setOrderingGroups(sharedViewModel.getOrder())
        val orderingGroupList: List<PointsOfService>? =
            filteredOrderInfo?.get(0)?.pointsOfService
        sharedViewModel.setPointsOfService(orderingGroupList)
        // Groups Points of Service from ordering info to get total number of ordering groups.
        val mapOrderingGroupList = orderingGroupList?.groupBy { it.pointOfServiceOrderingGroupNo }
        // Final list to enable displaying of correct Ordering Groups
        val groupedOrderingGroupList = mapOrderingGroupList?.values?.flatten()
        // Gets Ordering Group data
        val order = sharedViewModel.getOrderingGroups()
        // filters Ordering Groups to a list, based on the Points of service for the delivery address selected. This is then passed to the PosGroup adapter class.
        val orderGroupDescription = order?.filter { orderingGroupGroupNo ->
            groupedOrderingGroupList?.any { id -> id.pointOfServiceOrderingGroupNo == orderingGroupGroupNo.orderingGroupNo }
                ?: true
        }

        val adapter =
            OrderingGroupAdapter(OrderingGroupAdapter.OrderingGroupListener { orderingGroup ->
                orderingGroupViewModel.onOrderingGroupClicked(orderingGroup)
                orderingGroupViewModel.navigateToOrderingGroup.observe(
                    viewLifecycleOwner,
                    Observer { orderingGroupNo ->
                        orderingGroupNo?.let {
                            this.findNavController().navigate(
                                PosGroupFragmentDirections.actionPosGroupFragmentToPosFragment(
                                    orderingGroup.orderingGroupNo, orderingGroup.orderingGroupDescription
                                )
                            )
                            orderingGroupViewModel.onOrderingGroupNavigated()
                        }
                    })
            })
        adapter.submitList(orderGroupDescription)

        binding.orderingGroupSelection.adapter = adapter
        recyclerView.adapter = adapter
*/
    }
}
