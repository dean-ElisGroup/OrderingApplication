package com.elis.orderingapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.adapters.DeliveryAddressAdapter
import com.elis.orderingapplication.databinding.FragmentDeliveryAddressBinding
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.viewModels.DeliveryAddressViewModel
import com.elis.orderingapplication.viewModels.ParamsViewModel


class DeliveryAddressFragment : Fragment() {

    private lateinit var binding: FragmentDeliveryAddressBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()

    private val deliveryAddressViewModel: DeliveryAddressViewModel by lazy {
        ViewModelProvider(this)[DeliveryAddressViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDeliveryAddressBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.deliveryAddressModel = deliveryAddressViewModel
        binding.viewModel = sharedViewModel

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

        val recyclerView: RecyclerView = binding.deliveryAddressSelection
        sharedViewModel.setDeliveryAddress(sharedViewModel.getOrder())

        val deliveryAddressList: List<DeliveryAddress>? = sharedViewModel.getDeliveryAddresses()

        val adapter = DeliveryAddressAdapter()
        adapter.submitList(deliveryAddressList)

        binding.deliveryAddressSelection.adapter = adapter
        recyclerView.adapter = adapter
    }
}


