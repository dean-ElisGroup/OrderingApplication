package com.elis.orderingapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.elis.orderingapplication.adapters.OrderInfoAdapter
import com.elis.orderingapplication.databinding.FragmentDeliveryAddressBinding
import com.elis.orderingapplication.model.OrderingInfoResponse
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.OrderInfo
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import com.elis.orderingapplication.viewModels.LoginViewModel
import com.elis.orderingapplication.viewModels.LoginViewModelFactory
import com.elis.orderingapplication.viewModels.ParamsViewModel
import java.util.ArrayList


class DeliveryAddressFragment : Fragment() {

    private lateinit var binding: FragmentDeliveryAddressBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_delivery_address, container, false)
        binding?.apply { viewModel = sharedViewModel }
        binding.toolbar.title = getString(R.string.delivery_address_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            view?.let { it ->
                Navigation.findNavController(it)
                    .navigate(R.id.action_deliveryAddressFragment_to_landingPageFragment)
            }
        }
        // Inflate the layout for this fragment
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply { viewModel = sharedViewModel }


        val recyclerview = binding.deliveryAddressSelection
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        val orderingInfoResponse = sharedViewModel.getOrder()?.data
        val data = ArrayList<OrderInfo?>()
        data.add(orderingInfoResponse)
        val orderAdapter = OrderInfoAdapter(data)
        recyclerview.adapter = orderAdapter


    }
}


