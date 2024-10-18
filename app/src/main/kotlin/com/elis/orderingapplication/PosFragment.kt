package com.elis.orderingapplication

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.elis.orderingapplication.databinding.FragmentPosBinding
import com.elis.orderingapplication.pojo2.PointsOfService
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.viewModels.PosViewModel
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.elis.orderingapplication.adapters.listAdapters.PointOfServiceAdapter
import com.elis.orderingapplication.constants.Constants.Companion.POINT_OF_SERVICE_NAME_KEY
import com.elis.orderingapplication.constants.Constants.Companion.SHOW_BANNER
import com.elis.orderingapplication.utils.DeviceInfo
import com.elis.orderingapplication.utils.DeviceInfoDialog
import com.elis.orderingapplication.utils.FlavorBannerUtils
import com.elis.orderingapplication.viewModels.SharedViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PosFragment : Fragment(), PointOfServiceAdapter.TotalPOSCallback {

    private var _binding: FragmentPosBinding? = null
    private val binding: FragmentPosBinding get() = _binding!!
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private val args: PosFragmentArgs by navArgs()
    private lateinit var pointOfServiceAdapter: PointOfServiceAdapter
    private val posViewModel: PosViewModel by viewModels {
        SharedViewModelFactory(sharedViewModel, requireActivity().application)
    }
    private lateinit var recyclerView: RecyclerView
    private var deliveryAddressForArgs: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_pos, container, false)
        binding.toolbar.title = getString(R.string.pos_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setTitleTextAppearance(requireContext(), R.style.titleTextStyle)
        binding.toolbar.setNavigationOnClickListener {
            view?.let { it ->
                val action = PosFragmentDirections.actionPosFragmentToPosGroupFragment(
                    // Pass arguments here
                    deliveryAddressForArgs
                )
                Navigation.findNavController(it).navigate(action)
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
                    findNavController().navigate(R.id.action_posFragment_to_landingPageFragment)
                    true
                }

                else -> false
            }
        }

        // Set delivery address and ordering group directly from args
        binding.deliveryAddress.text = args.deliveryAddressName
        binding.orderingGroup.text = args.orderingGroupName

        // Sets ordering group and ordering name to shared ViewModel
        args.orderingGroupNo?.let { sharedViewModel.setOrderingGroupNo(it) }
        sharedViewModel.setOrderingGroupName(args.orderingGroupName)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFlavorBanner()
        setupRecyclerView()
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
        recyclerView = binding.posSelection
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.pos_card_spacing)
        recyclerView.addItemDecoration(CardViewDecoration(spacingInPixels))

        pointOfServiceAdapter =
            PointOfServiceAdapter(object : PointOfServiceAdapter.MyClickListener {
                override fun onItemClick(myData: PointsOfService) {
                    handlePosClick(myData)
                }
            }, this)
        binding.posSelection.adapter = pointOfServiceAdapter
    }

    private fun handlePosClick(pos: PointsOfService) {
        posViewModel.onPosClicked(pos)
        sharedViewModel.setPosNum(pos.pointOfServiceNo)
        val updatedArgsBundle = sharedViewModel.argsBundleFromTest.value?.apply {
            putString(POINT_OF_SERVICE_NAME_KEY, pos.pointOfServiceName)
        } ?: Bundle().apply {
            putString(POINT_OF_SERVICE_NAME_KEY, pos.pointOfServiceName)
        }
        sharedViewModel.argsBundleFromTest.value = updatedArgsBundle

        viewLifecycleOwner.lifecycleScope.launch {
            posViewModel.navigateToPos.collect { pointOfService ->
                pointOfService?.let { navigateToOrderFragment(it) }
            }
        }
    }

    private fun navigateToOrderFragment(pos: PointsOfService) {
        findNavController().navigate(
            PosFragmentDirections.actionPosFragmentToOrderFragment(
                pos.pointOfServiceNo,
                pos.deliveryAddressName,
                pos.pointOfServiceName
            )
        )
        posViewModel.onPosNavigated()
    }

    /*Added the onStart override function to allow the better observing of live data when the fragment starts. The previous code could have caused an issue with trying to get data after the fragment
    * had loaded. This is linked to an ongoing issue that's being flagged by the Firebase crashlytics tool*/
    override fun onStart() {
        super.onStart()
        viewLifecycleOwner.lifecycleScope.launch {
            posViewModel.uiState.collectLatest { state ->
                when (state) {
                    PosViewModel.PosUiState.Loading -> {
                        binding.progressBar.visibility = VISIBLE
                    }

                    is PosViewModel.PosUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.totalPOS.visibility = VISIBLE

                        pointOfServiceAdapter.setData(state.pointsOfService)

                        if (state.pointsOfService.isEmpty()) {
                            noOrderDialog()
                        }
                    }

                    PosViewModel.PosUiState.Error -> {
                        noOrderDialog()
                    }
                }
            }
        }
    }

    private fun noOrderDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("No orders")
        builder.setIcon(R.drawable.outline_error_24)
        builder.setMessage("There are no Points of service with orders for selection.")
        builder.setPositiveButton("OK") { dialog, which ->
            findNavController().navigate(R.id.action_posFragment_to_posGroupFragment)
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onTotalPOSUpdated(totalPOS: Int) {
        // Update the totalPOS value in your PosFragment
        binding.totalPos1 = totalPOS.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference
    }
}