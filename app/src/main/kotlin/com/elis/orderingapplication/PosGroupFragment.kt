package com.elis.orderingapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.FragmentPosGroupBinding
import com.elis.orderingapplication.viewModels.OrderingGroupViewModel
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.adapters.listAdapters.OrderingGroupAdapter
import com.elis.orderingapplication.constants.Constants.Companion.DELIVERY_ADDRESS_NAME_KEY
import com.elis.orderingapplication.constants.Constants.Companion.ORDERING_GROUP_KEY
import com.elis.orderingapplication.constants.Constants.Companion.SHOW_BANNER
import com.elis.orderingapplication.pojo2.JoinOrderingGroup
import com.elis.orderingapplication.utils.DeviceInfo
import com.elis.orderingapplication.utils.DeviceInfoDialog
import com.elis.orderingapplication.utils.FlavorBannerUtils
import com.elis.orderingapplication.viewModels.SharedViewModelFactory

class PosGroupFragment : Fragment() {

    private var _binding: FragmentPosGroupBinding? = null
    private val binding: FragmentPosGroupBinding get() = _binding!!
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private lateinit var orderingGroupAdapter: OrderingGroupAdapter
    private val orderingGroupViewModel: OrderingGroupViewModel by viewModels {
        SharedViewModelFactory(sharedViewModel, requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPosGroupBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.orderingGroupViewModel = orderingGroupViewModel
        binding.sharedViewModel = sharedViewModel
        binding.toolbar.title = getString(R.string.pos_group_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setTitleTextAppearance(requireContext(), R.style.titleTextStyle)
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFlavorBanner()
        setupRecyclerView()
        setupDeliveryAddress()
        observeOrderingGroupData()
    }

    private fun setupFlavorBanner() {
        if (SHOW_BANNER) {
            FlavorBannerUtils.setupFlavorBanner(
                resources,
                requireContext(),
                binding,
                sharedViewModel
            )
            binding.debugBanner.visibility = VISIBLE
        }
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = binding.orderingGroupSelection
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        recyclerView.addItemDecoration(CardViewDecoration(spacingInPixels))

        orderingGroupAdapter = OrderingGroupAdapter(object : OrderingGroupAdapter.MyClickListener {
            override fun onItemClick(myData: JoinOrderingGroup) {
                handleOrderingGroupClick(myData)
            }
        })
        binding.orderingGroupSelection.adapter = orderingGroupAdapter
    }

    private fun setupDeliveryAddress() {
        val deliveryAddressFromArgs =
            sharedViewModel.argsBundleFromTest.value?.getString(DELIVERY_ADDRESS_NAME_KEY, "")
        binding.deliveryAddress.text = deliveryAddressFromArgs ?: run {
            sharedViewModel.argsBundleFromTest.observe(viewLifecycleOwner) { bundle ->
                binding.deliveryAddress.text = bundle.getString(DELIVERY_ADDRESS_NAME_KEY, "")
            }
            ""
        }
    }

    private fun observeOrderingGroupData() {
        orderingGroupViewModel.orderingGroup.observe(viewLifecycleOwner) { orderingGroup ->
            orderingGroupAdapter.setData(orderingGroup)
        }
    }

    private fun handleOrderingGroupClick(orderingGroup: JoinOrderingGroup) {
        orderingGroupViewModel.onOrderingGroupClicked(orderingGroup)
        sharedViewModel.setOrderingGroupNo(orderingGroup.orderingGroupNo)
        sharedViewModel.setDeliveryAddressNum(orderingGroup.deliveryAddressNo)
        val updatedArgsBundle = sharedViewModel.argsBundleFromTest.value?.apply {
            putString(ORDERING_GROUP_KEY, orderingGroup.orderingGroupDescription)
        } ?: Bundle().apply {
            putString(ORDERING_GROUP_KEY, orderingGroup.orderingGroupDescription)
        }
        sharedViewModel.argsBundleFromTest.value = updatedArgsBundle
        orderingGroupViewModel.navigateToOrderingGroup.observe(viewLifecycleOwner) { group ->
            group?.let { navigateToPosFragment(it) }
        }
    }

    private fun navigateToPosFragment(orderingGroup: JoinOrderingGroup) {
        findNavController().navigate(
            PosGroupFragmentDirections.actionPosGroupFragmentToPosFragment(
                orderingGroup.orderingGroupNo,
                orderingGroup.orderingGroupDescription
            )
        )
        orderingGroupViewModel.onOrderingGroupNavigated()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
