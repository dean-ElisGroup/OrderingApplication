package com.elis.orderingapplication.sendOrder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.CardViewDecoration
import com.elis.orderingapplication.R
import com.elis.orderingapplication.adapters.listAdapters.DeliveryAdapter
import com.elis.orderingapplication.databinding.FragmentDeliveryAddressBinding
import com.elis.orderingapplication.databinding.FragmentSendOrderDeliveryAddressBinding
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.viewModels.DeliveryAddressViewModel
import com.elis.orderingapplication.viewModels.ParamsViewModel

class SendDeliveryAddressFragment : Fragment() {

    private lateinit var binding: FragmentSendOrderDeliveryAddressBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private lateinit var deliveryAdapter: DeliveryAdapter
    private val deliveryAddressViewModel: SendDeliveryAddressViewModel by lazy {
        ViewModelProvider(this)[SendDeliveryAddressViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSendOrderDeliveryAddressBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = sharedViewModel

        binding.toolbar.title = getString(R.string.send_order_delivery_address)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            view?.let { it ->
                Navigation.findNavController(it)
                    .navigate(R.id.action_sendDeliveryAddressFragment_to_landingPageFragment)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.deliveryAddressSelection
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        val itemSpacingDecoration = CardViewDecoration(spacingInPixels)
        recyclerView.addItemDecoration(itemSpacingDecoration)
        sharedViewModel.setDeliveryAddress(sharedViewModel.getOrder())

        deliveryAdapter = DeliveryAdapter(object : DeliveryAdapter.MyClickListener {
            override fun onItemClick(myData: DeliveryAddress) {
                deliveryAddressViewModel.onDeliveryAddressClicked(myData.deliveryAddressNo)
                // Stores the selected delivery address no to the shared view model as a LiveData string
                sharedViewModel.setDeliveryAddressNum(myData.deliveryAddressNo)
                val data = Bundle().apply {
                    putString("DELIVERY_ADDRESS", myData.deliveryAddressNo)
                    putString("DELIVERY_ADDRESS_NAME", myData.deliveryAddressName)
                }
                sharedViewModel.argsBundleFromTest.value = data
                deliveryAddressViewModel.navigateToOrderingGroup.observe(
                    viewLifecycleOwner,
                    Observer { deliveryAddressNo ->
                        deliveryAddressNo?.let {
                            findNavController().navigate(
                                SendDeliveryAddressFragmentDirections.actionSendDeliveryAddressFragmentToSendOrderOrderFragment(
                                    deliveryAddressNo
                                )
                            )
                            deliveryAddressViewModel.onDeliveryAddressNavigated()
                        }
                    })

            }
        })

        binding.deliveryAddressSelection.adapter = deliveryAdapter

        // Observe the LiveData from the ViewModel
        deliveryAddressViewModel.entities.observe(viewLifecycleOwner) { deliveryAddresses ->
            deliveryAdapter.setData(deliveryAddresses)
        }
    }
}


