package com.elis.orderingapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.adapters.listAdapters.DeliveryAdapter
import com.elis.orderingapplication.constants.Constants.Companion.SHOW_BANNER
import com.elis.orderingapplication.databinding.FragmentDeliveryAddressBinding
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.utils.DeviceInfo
import com.elis.orderingapplication.utils.DeviceInfoDialog
import com.elis.orderingapplication.viewModels.DeliveryAddressViewModel
import com.elis.orderingapplication.viewModels.ParamsViewModel

class DeliveryAddressFragment : Fragment() {

    private lateinit var binding: FragmentDeliveryAddressBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private lateinit var deliveryAdapter: DeliveryAdapter
    private val deliveryAddressViewModel: DeliveryAddressViewModel by lazy {
        ViewModelProvider(this)[DeliveryAddressViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDeliveryAddressBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = sharedViewModel
        binding.toolbar.title = getString(R.string.delivery_address_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setTitleTextAppearance(requireContext(),R.style.titleTextStyle)
        binding.toolbar.setNavigationOnClickListener {
            view?.let { it ->
                Navigation.findNavController(it)
                    .navigate(R.id.action_deliveryAddressFragment_to_landingPageFragment)
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
                    findNavController().navigate(R.id.action_deliveryAddressFragment_to_landingPageFragment)
                    true
                }

                else -> false
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Calls function to set the environment banner
        if(SHOW_BANNER) {
            setFlavorBanner()
            binding.debugBanner.visibility = VISIBLE
        }
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
                                DeliveryAddressFragmentDirections.actionDeliveryAddressFragmentToPosGroupFragment(
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
}


