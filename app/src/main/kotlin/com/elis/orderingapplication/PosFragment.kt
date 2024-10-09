package com.elis.orderingapplication


import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
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
import com.elis.orderingapplication.constants.Constants.Companion.SHOW_BANNER
import com.elis.orderingapplication.databinding.FragmentOrderBinding
import com.elis.orderingapplication.pojo2.PointsOfServiceWithTotalOrders
import com.elis.orderingapplication.utils.DeviceInfo
import com.elis.orderingapplication.utils.DeviceInfoDialog
import com.elis.orderingapplication.utils.FlavorBannerUtils
import com.elis.orderingapplication.viewModels.SharedViewModelFactory
import kotlinx.coroutines.launch
import org.junit.runner.manipulation.Ordering

import java.util.Locale

class PosFragment : Fragment(), PointOfServiceAdapter.TotalPOSCallback {

    private var _binding: FragmentPosBinding? = null
    private val binding: FragmentPosBinding get() = _binding!!
    //private lateinit var binding: FragmentPosBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private val args: PosFragmentArgs by navArgs()
    private lateinit var pointOfServiceAdapter: PointOfServiceAdapter
    private val posViewModel: PosViewModel by viewModels {
        SharedViewModelFactory(sharedViewModel, requireActivity().application)
    }
    private lateinit var recyclerView: RecyclerView
    private var deliveryAddressForArgs: String = ""
    private var orderingGroupForArgs: String? = null
    private lateinit var searchView: SearchView

    private var posObserver: Observer<List<PointsOfServiceWithTotalOrders>>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_pos, container, false)

        binding.sharedViewModel = sharedViewModel
        binding.posViewModel = posViewModel
        binding.toolbar.title = getString(R.string.pos_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setTitleTextAppearance(requireContext(),R.style.titleTextStyle)
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

        val deliveryAddressFromArgs =
            sharedViewModel.argsBundleFromTest.value?.getString("DELIVERY_ADDRESS_NAME", "")
        orderingGroupForArgs =
            sharedViewModel.argsBundleFromTest.value?.getString("ORDERING_GROUP", "")
        if (deliveryAddressFromArgs != null) {
            binding.deliveryAddress.text = deliveryAddressFromArgs
            binding.orderingGroup.text = orderingGroupForArgs
        } else {
            sharedViewModel.argsBundleFromTest.observe(viewLifecycleOwner, Observer {
                deliveryAddressForArgs = it.getString("DELIVERY_ADDRESS_NAME", "")
                orderingGroupForArgs = it.getString("ORDERING_GROUP", "")
                binding.deliveryAddress.text = deliveryAddressForArgs
                binding.orderingGroup.text = orderingGroupForArgs
            })
        }

        // Sets ordering group and ordering name to shared ViewModel
        args.orderingGroupNo?.let { sharedViewModel.setOrderingGroupNo(it) }
        sharedViewModel.setOrderingGroupName(args.orderingGroupName)

        // Set up the SearchView
        /*searchView = binding.searchView!!
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterPOSList(query)
                searchView.clearFocus()
                hideSearchKeyboard()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search query text changes
                filterPOSList(newText)
                return true
            }
        })*/
        // Inflate the layout for this fragment

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(SHOW_BANNER) {
            FlavorBannerUtils.setupFlavorBanner(
                resources,
                requireContext(),
                binding,
                sharedViewModel
            )
            binding.debugBanner.visibility = VISIBLE
        }
        recyclerView = binding.posSelection

        binding.progressBar.visibility = View.VISIBLE

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.pos_card_spacing)
        val itemSpacingDecoration = CardViewDecoration(spacingInPixels)
        recyclerView.addItemDecoration(itemSpacingDecoration)

        pointOfServiceAdapter =
            PointOfServiceAdapter(object : PointOfServiceAdapter.MyClickListener {
                override fun onItemClick(myData: PointsOfService) {
                    posViewModel.onPosClicked(myData)
                    sharedViewModel.setPosNum(myData.pointOfServiceNo)
                    val currentArgsBundle = sharedViewModel.argsBundleFromTest.value ?: Bundle()
                    currentArgsBundle.putString("POINT_OF_SERVICE_NAME", myData.pointOfServiceName)
                    sharedViewModel.argsBundleFromTest.value = currentArgsBundle
                    posViewModel.navigateToPos.observe(
                        viewLifecycleOwner,
                        Observer { pointOfService ->
                            pointOfService?.let {
                                findNavController().navigate(
                                    PosFragmentDirections.actionPosFragmentToOrderFragment(
                                        pointOfService.pointOfServiceNo,
                                        pointOfService.deliveryAddressName,
                                        pointOfService.pointOfServiceName
                                    )
                                )
                                posViewModel.onPosNavigated()
                            }
                        })

                }
            }, this)
        binding.posSelection.adapter = pointOfServiceAdapter
    }

    /*Added the onStart override function to allow the better observing of live data when the fragment starts. The previous code could have caused an issue with trying to get data after the fragment
    * had loaded. This is linked to an ongoing issue that's being flagged by the Firebase crashlytics tool*/
    override fun onStart() {
        super.onStart()
        // Observe the LiveData from the ViewModel
        posViewModel.pointsOfService.observe(
            viewLifecycleOwner,
            Observer { pointsOfServiceWithTotalOrders ->
                viewLifecycleOwner.lifecycleScope.launch {
                    requireActivity().window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                    binding.progressBar.visibility = View.GONE
                    binding.totalPOS.visibility = View.VISIBLE
                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    pointOfServiceAdapter.setData(pointsOfServiceWithTotalOrders)

                    if (pointsOfServiceWithTotalOrders.isEmpty()) {
                        noOrderDialog()
                    }
                }
            })
    }
    private fun noOrderDialog(){
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

    /*private fun filterPOSList(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            // If the search query is empty, show the entire list
            posViewModel.pointsOfService.value
        } else {
            // Filter the list based on the search query
            val filterPattern = query.toString().trim().lowercase(Locale.ROOT)
            posViewModel.pointsOfService.value?.filter {
                it.pointsOfService.pointOfServiceName?.lowercase(Locale.ROOT)?.contains(filterPattern) == true
            }
        }

        // Update the adapter with the filtered list
        if (filteredList != null) {
            pointOfServiceAdapter.setData(filteredList)
        }
    }

    private fun hideSearchKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }*/
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference
    }
}