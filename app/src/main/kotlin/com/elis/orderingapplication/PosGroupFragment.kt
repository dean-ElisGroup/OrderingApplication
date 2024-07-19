package com.elis.orderingapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.FragmentPosGroupBinding
import com.elis.orderingapplication.viewModels.OrderingGroupViewModel
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.adapters.listAdapters.OrderingGroupAdapter
import com.elis.orderingapplication.constants.Constants.Companion.SHOW_BANNER
import com.elis.orderingapplication.pojo2.JoinOrderingGroup
import com.elis.orderingapplication.utils.DeviceInfo
import com.elis.orderingapplication.utils.DeviceInfoDialog
import com.elis.orderingapplication.viewModels.SharedViewModelFactory

class PosGroupFragment : Fragment() {

    private lateinit var binding: FragmentPosGroupBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private val args: PosGroupFragmentArgs by navArgs()
    private lateinit var orderingGroupAdapter: OrderingGroupAdapter
    private var deliveryAddressForArgs: String = ""
    private val orderingGroupViewModel: OrderingGroupViewModel by viewModels {
        SharedViewModelFactory(sharedViewModel, requireActivity().application)
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
        binding.toolbar.setTitleTextAppearance(requireContext(),R.style.titleTextStyle)
        binding.toolbar.setNavigationOnClickListener {
            view?.let { it ->
                Navigation.findNavController(it)
                    .navigate(R.id.action_posGroupFragment_to_deliveryAddressFragment)
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
                    findNavController().navigate(R.id.action_posGroupFragment_to_landingPageFragment)
                    true
                }

                else -> false
            }
        }

        val deliveryAddressFromArgs =
            sharedViewModel.argsBundleFromTest.value?.getString("DELIVERY_ADDRESS_NAME", "")
        if (deliveryAddressFromArgs != null) {
            binding.deliveryAddress.text = deliveryAddressFromArgs
        } else {
            sharedViewModel.argsBundleFromTest.observe(viewLifecycleOwner, Observer {
                deliveryAddressForArgs = it.getString("DELIVERY_ADDRESS_NAME", "")
                binding.deliveryAddress.text = deliveryAddressForArgs
            })
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(SHOW_BANNER) {
            setFlavorBanner()
            binding.debugBanner.visibility = VISIBLE
        }
        val recyclerView: RecyclerView = binding.orderingGroupSelection
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        val itemSpacingDecoration = CardViewDecoration(spacingInPixels)
        recyclerView.addItemDecoration(itemSpacingDecoration)

        orderingGroupAdapter = OrderingGroupAdapter(object : OrderingGroupAdapter.MyClickListener {
            override fun onItemClick(myData: JoinOrderingGroup) {
                orderingGroupViewModel.onOrderingGroupClicked(myData)
                sharedViewModel.setOrderingGroupNo(myData.orderingGroupNo)
                sharedViewModel.setDeliveryAddressNum(myData.deliveryAddressNo)
                val currentArgsBundle = sharedViewModel.argsBundleFromTest.value ?: Bundle()
                val updatedArgsBundle = currentArgsBundle.apply {
                    putString("ORDERING_GROUP", myData.orderingGroupDescription)
                }
                sharedViewModel.argsBundleFromTest.value = updatedArgsBundle
                orderingGroupViewModel.navigateToOrderingGroup.observe(
                    viewLifecycleOwner,
                    Observer { orderingGroup ->
                        orderingGroup?.let {
                            findNavController().navigate(
                                PosGroupFragmentDirections.actionPosGroupFragmentToPosFragment(
                                    orderingGroup.orderingGroupNo,
                                    orderingGroup.orderingGroupDescription
                                )
                            )
                            orderingGroupViewModel.onOrderingGroupNavigated()
                        }
                    })

            }
        })

        binding.orderingGroupSelection.adapter = orderingGroupAdapter

        // Observe the LiveData from the ViewModel
        orderingGroupViewModel.orderingGroup.observe(viewLifecycleOwner) { orderingGroup ->
            orderingGroupAdapter.setData(orderingGroup)
        }
    }

    private fun setFlavorBanner() {
        when (sharedViewModel.flavor.value) {
            "development" -> {
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
            "production" -> {
                binding.debugBanner.visibility = View.GONE
                binding.bannerText.visibility = View.GONE
                binding.debugBanner.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.elis_transparent
                    )
                )
            }
            "staging" -> {
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
}
