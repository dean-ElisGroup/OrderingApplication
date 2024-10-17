package com.elis.orderingapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.elis.orderingapplication.constants.Constants.Companion.SHOW_BANNER
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.databinding.FragmentLandingPageBinding
import com.elis.orderingapplication.model.LogoutRequest
import com.elis.orderingapplication.repositories.AppRepository
import com.elis.orderingapplication.utils.ApiResponse
import com.elis.orderingapplication.utils.DeviceInfo
import com.elis.orderingapplication.utils.DeviceInfoDialog
import com.elis.orderingapplication.utils.FlavorBannerUtils
import com.elis.orderingapplication.utils.InternetCheck
import com.elis.orderingapplication.viewModels.AppViewModelFactory
import com.elis.orderingapplication.viewModels.LandingPageViewModel
import com.elis.orderingapplication.viewModels.ParamsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LandingPageFragment :Fragment() {

    private lateinit var binding: FragmentLandingPageBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private val landingPageView: LandingPageViewModel by viewModels {
        AppViewModelFactory(
            sharedViewModel,
            requireActivity().application,
            AppRepository() // Repository created in the factory
        )
    }
    private lateinit var database: OrderInfoDatabase
    private var isLogoutInProgress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            database = Room.databaseBuilder(
                requireContext(),
                OrderInfoDatabase::class.java,
                "order_info_database"
            ).build()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            Toast.makeText(
                activity,
                "Please use the logout function",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_landing_page, container, false)
        // Clears hold on UI interaction when progress bar is visible
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = sharedViewModel
            ordersButton.setOnClickListener {
                findNavController().navigate(R.id.action_landingPageFragment_to_deliveryAddressFragment)
            }
            buttonSendOrders.setOnClickListener {
                findNavController().navigate(R.id.action_landingPageFragment_to_sendDeliveryAddressFragment)
            }
            overflowMenu2.setOnClickListener {
                val deviceInfo = DeviceInfo(requireContext())
                DeviceInfoDialog.showAlertDialog(requireContext(), deviceInfo.getDeviceInfo())
            }
            buttonLogout.setOnClickListener { performLogout() }
        }

        if (SHOW_BANNER) {
            FlavorBannerUtils.setupFlavorBanner(resources, requireContext(), binding, sharedViewModel)
            binding.debugBanner.visibility = VISIBLE
        }
    }

    private fun performLogout() {
        if (!isLogoutInProgress) {
            isLogoutInProgress = true
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                if (checkInternetAvailability()) {
                    val logoutSessionKey = LogoutRequest(sharedViewModel.getSessionKey())
                    landingPageView.logoutUser(logoutSessionKey)
                    landingPageView.userLogoutResponse.observe(viewLifecycleOwner) { observeResponse ->
                        when (observeResponse) {
                            is ApiResponse.Success -> {
                                // Clear back stack and navigate to login
                                val navOptions = NavOptions.Builder()
                                    .setPopUpTo(R.id.landingPageFragment, true)
                                    .build()
                                findNavController().navigate(R.id.loginFragment, null, navOptions)

                                Toast.makeText(
                                    requireContext(),
                                    "You have been logged out",
                                    Toast.LENGTH_LONG
                                ).show()
                                isLogoutInProgress = false
                                sharedViewModel.clearData()
                            }
                            is ApiResponse.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    observeResponse.message,
                                    Toast.LENGTH_LONG
                                ).show()
                                isLogoutInProgress = false
                            }
                            else -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Unknown logout issue",
                                    Toast.LENGTH_LONG
                                ).show()
                                isLogoutInProgress = false
                            }
                        }
                    }
                } else {
                    showNoInternetConnectionError()
                    isLogoutInProgress = false
                }
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Logout request already in progress",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private suspend fun checkInternetAvailability(): Boolean = withContext(Dispatchers.IO) {
        InternetCheck.isInternetAvailable()}

    private fun showNoInternetConnectionError() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("No Internet Connection")
        builder.setMessage("You need an active internet connection to log out. Please check your internet connection and try again.")
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }
}