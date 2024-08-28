package com.elis.orderingapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
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
        if(SHOW_BANNER) {
            setFlavorBanner()
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

    private suspend fun checkInternetAvailability(): Boolean {
        return withContext(Dispatchers.IO) {
            val isInternetAvailable = InternetCheck.isInternetAvailable()
            isInternetAvailable
        }
    }
}