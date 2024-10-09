package com.elis.orderingapplication

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.room.Room
import com.elis.orderingapplication.constants.Constants.Companion.SHOW_BANNER
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.databinding.FragmentLandingPageBinding
import com.elis.orderingapplication.model.LogoutRequest
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import com.elis.orderingapplication.utils.DeviceInfo
import com.elis.orderingapplication.utils.DeviceInfoDialog
import com.elis.orderingapplication.utils.FlavorBannerUtils
import com.elis.orderingapplication.utils.InternetCheck
import com.elis.orderingapplication.viewModels.ArticleEntryViewModelFactory
import com.elis.orderingapplication.viewModels.LandingPageViewModel
import com.elis.orderingapplication.viewModels.ParamsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LandingPageFragment : Fragment() {

    private lateinit var binding: FragmentLandingPageBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    lateinit var database: OrderInfoDatabase
    private var isLogoutInProgress = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_landing_page, container, false)
        // Clears hold on UI interaction when progress bar is visible
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding.overflowMenu2.setOnClickListener {
            val deviceInfo = DeviceInfo(requireContext())
            DeviceInfoDialog.showAlertDialog(requireContext(), deviceInfo.getDeviceInfo())
        }

        val rep = UserLoginRepository()
        val provider = ArticleEntryViewModelFactory(
            sharedViewModel,
            requireActivity().application,
            rep
        )
        val landingPageView = ViewModelProvider(this, provider)[LandingPageViewModel::class.java]

        // Inflate the layout for this fragment
        with(binding) {
            ordersButton.setOnClickListener {
                view?.let {
                    Navigation.findNavController(it)
                        .navigate(R.id.action_landingPageFragment_to_deliveryAddressFragment)
                }
            }
            buttonSendOrders.setOnClickListener {
                view?.let {
                    Navigation.findNavController(it)
                        .navigate(R.id.action_landingPageFragment_to_sendDeliveryAddressFragment)
                }
            }

            buttonLogout.setOnClickListener {
                if (!isLogoutInProgress) {
                    isLogoutInProgress = true
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                        if (checkInternetAvailability()) {
                            val logoutSessionKey = LogoutRequest(sharedViewModel.getSessionKey())
                            val response = landingPageView.getUserLogout(logoutSessionKey)
                            landingPageView.userLoginResponse.observe(viewLifecycleOwner) { observeResponse ->
                                when (observeResponse) {
                                    is ApiResponse.Success -> {
                                        view?.let {
                                            Navigation.findNavController(it)
                                                .navigate(R.id.action_landingPageFragment_to_loginFragment)
                                            Toast.makeText(
                                                requireContext(),
                                                "You have been logged out",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            isLogoutInProgress = false
                                            sharedViewModel.clearData()
                                            (activity as MainActivity).restartActivity()


                                        }
                                    }

                                    is ApiResponse.Error -> {
                                        Toast.makeText(
                                            requireContext(),
                                            observeResponse.message,
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
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
                    // Logout request is already in progress, show a message or handle it as per your requirements
                    Toast.makeText(
                        requireContext(),
                        "Logout request already in progress",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            return binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        database = Room.databaseBuilder(
            requireContext(),
            OrderInfoDatabase::class.java,
            "order_info_database"
        ).build()
        binding.apply { viewModel = sharedViewModel }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            Toast.makeText(
                activity,
                "Please use the logout function",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private suspend fun checkInternetAvailability(): Boolean {
        return withContext(Dispatchers.IO) {
            val isInternetAvailable = InternetCheck.isInternetAvailable()
            isInternetAvailable
        }
    }

    private fun showNoInternetConnectionError() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("No Internet Connection")
        builder.setMessage("You need an active internet connection to log out. Please check your internet connection and try again.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}