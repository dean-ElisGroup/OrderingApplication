package com.elis.orderingapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.room.Room
import com.elis.orderingapplication.database.OrderInfo
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.databinding.FragmentLandingPageBinding
import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.LogoutRequest
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import com.elis.orderingapplication.viewModels.ArticleEntryViewModelFactory
import com.elis.orderingapplication.viewModels.LandingPageViewModel
import com.elis.orderingapplication.viewModels.LoginViewModel
import com.elis.orderingapplication.viewModels.LoginViewModelFactory
import com.elis.orderingapplication.viewModels.ParamsViewModel

class LandingPageFragment : Fragment() {

    private lateinit var binding: FragmentLandingPageBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private lateinit var landingPageView: LandingPageViewModel
    lateinit var database: OrderInfoDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_landing_page, container, false)
        // Clears hold on UI interaction when progress bar is visible
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

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
                view?.let { it ->
                    Navigation.findNavController(it)
                        .navigate(R.id.action_landingPageFragment_to_deliveryAddressFragment)
                }
            }
            buttonSendOrders.setOnClickListener {
                view?.let { it ->
                    Navigation.findNavController(it)
                        .navigate(R.id.action_landingPageFragment_to_sendDeliveryAddressFragment)
                }
            }

            buttonLogout.setOnClickListener {
                val logoutSessionKey = LogoutRequest(sharedViewModel.getSessionKey())
                val response = landingPageView.getUserLogout(logoutSessionKey)
                landingPageView.userLoginResponse.observe(viewLifecycleOwner) { response ->
                    when (response) {
                        is ApiResponse.Success -> {
                            view?.let { it ->
                                Navigation.findNavController(it)
                                    .navigate(R.id.action_landingPageFragment_to_loginFragment)
                                Toast.makeText(
                                    requireContext(),
                                    "You have been logged out",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }

                        is ApiResponse.Error -> {
                            Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG)
                                .show()

                        }

                        else ->
                            Toast.makeText(
                                requireContext(),
                                "Unknown logout issue",
                                Toast.LENGTH_LONG
                            ).show()
                    }
                }
            }
            return binding.root //inflater.inflate(layout.fragment_landing_page, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        database = Room.databaseBuilder(
            requireContext(),
            OrderInfoDatabase::class.java,
            "order_info_database"
        ).build()
        binding.apply { viewModel = sharedViewModel }
        //apiCall2(sharedViewModel.session_key.value.toString())
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.login_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.login_menu_overflow -> {
                        // loadTasks(true)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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


}