package com.elis.orderingapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.elis.orderingapplication.databinding.FragmentDeliveryAddressBinding
import com.elis.orderingapplication.viewModels.ParamsViewModel

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
        //binding.toolbar.inflateMenu(R.menu.login_menu)
        // Inflate the layout for this fragment
        return binding.root

    }
}