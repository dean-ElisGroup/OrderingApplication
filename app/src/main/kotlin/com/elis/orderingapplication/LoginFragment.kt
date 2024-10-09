package com.elis.orderingapplication

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.OrderingRequest
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.viewModels.LoginViewModel
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import com.elis.orderingapplication.constants.Constants.Companion.SHOW_BANNER
import com.elis.orderingapplication.databinding.FragmentLoginBinding
import com.elis.orderingapplication.model.OrderingLoginResponseStruct
import com.elis.orderingapplication.pojo2.OrderInfo
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import com.elis.orderingapplication.utils.DeviceInfo
import com.elis.orderingapplication.utils.DeviceInfoDialog
import com.elis.orderingapplication.utils.FlavorBannerUtils
import com.elis.orderingapplication.utils.InternetCheck
import com.elis.orderingapplication.viewModels.LoginViewModelFactory
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private lateinit var loginView: LoginViewModel
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var username: Editable? = null
    private var password: Editable? = null
    private var orderInfoLoading: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        //val view = binding.root
        sharedViewModel.setAppVersion(BuildConfig.VERSION_NAME)
        sharedViewModel.setFlavor(BuildConfig.FLAVOR)
        val loginRepository = UserLoginRepository()
        val provider = LoginViewModelFactory(loginRepository)
        val loginViewModel = ViewModelProvider(this, provider)[LoginViewModel::class.java]
        loginView = loginViewModel
        binding.apply {
            viewModel = loginView
            paramViewModel = sharedViewModel
            lifecycleOwner = this@LoginFragment
        }
        binding.lifecycleOwner = this
        orderInfoLoading = binding.orderInfoLoading
        // Sets info button to show device info dialog
        setupOverflowMenu()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeLogin()

        loginView.rootView = view
        loginView.orderInfoLoading = view.findViewById(R.id.order_info_loading)

        with(binding) {
            loginButton.setOnClickListener {
                checkInternetAndLogin(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            viewLifecycleOwner.lifecycleScope.launch {
                Toast.makeText(requireContext(), "Login is required.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupOverflowMenu() {
        binding.overflowMenu.setOnClickListener {
            val deviceInfo = DeviceInfo(requireContext())
            DeviceInfoDialog.showAlertDialog(requireContext(), deviceInfo.getDeviceInfo())
        }
    }

    private fun initializeLogin() {
        binding.date.text = loginView.getDate()
        sharedViewModel.setOrderDate(binding.date.text.toString())
        sharedViewModel.setAppVersion(BuildConfig.VERSION_NAME)
        sharedViewModel.setFlavor(BuildConfig.FLAVOR)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        FirebaseAnalytics.getInstance(requireContext()).setAnalyticsCollectionEnabled(true)
        FirebaseAnalytics.getInstance(requireContext()).setUserProperty("debug_mode", "true")
        if (SHOW_BANNER) {
            FlavorBannerUtils.setupFlavorBanner(
                resources,
                requireContext(),
                binding,
                sharedViewModel
            )
        }
    }

    private fun checkInternetAndLogin(view: View) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            if (checkInternetAvailability()) {
                if (checkUsernamePassword()) {
                    // Sets the required username and password to be sent within the Login Api call.
                    requireActivity().window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                    view.hideKeyboard()
                    // Gets and sets the user entered username and password.
                    val usernameText =
                        binding.username.text?.toString() ?: "" // username.text.toString()
                    val passwordText =
                        binding.password.text?.toString() ?: "" // password.text.toString()
                    val login = LoginRequest(usernameText, passwordText)
                    // Initiates the Login Api call, passing the above username and password.
                    loginView.resetLoginState(viewLifecycleOwner)
                    loginView.getUserLogin(login)
                    loginView.userLoginResponse.observe(viewLifecycleOwner) { response ->
                        handleLoginResponse(response)
                    }
                }
            } else {
                showNoInternetToast()
            }
        }
    }

    /*private fun showAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Internet Connection")
        builder.setMessage("There is currently no internet connection. Please check your connection and try again.")
        builder.setIcon(R.drawable.outline_error_24)
        builder.setPositiveButton("OK") { dialog, _ ->
            // Handle positive button click
            dialog.dismiss()
        }

        builder.setCancelable(true)

        val dialog = builder.create()
        dialog.show()
    }*/

    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun checkUsernamePassword(): Boolean {
        return if (TextUtils.isEmpty(binding.username.text) || TextUtils.isEmpty(binding.password.text)) {
            viewLifecycleOwner.lifecycleScope.launch {
                Toast.makeText(
                    requireContext(),
                    "Username or Password cannot be empty",
                    Toast.LENGTH_LONG
                ).show()
            }
            false
        } else true
    }

    private suspend fun checkInternetAvailability(): Boolean = withContext(Dispatchers.IO) {
        InternetCheck.isInternetAvailable()
    }

    private fun handleLoginResponse(response: ApiResponse<OrderingLoginResponseStruct>?) {
        if (response != null) {
            when (response) {
                is ApiResponse.Success -> {
                    orderInfoLoading?.visibility = VISIBLE
                    response.data?.sessionKey?.let { sessionKey ->
                        handleSessionKey(sessionKey)
                    }
                    // Logs successful login to Firebase Analytics
                    logLoginSuccess(username.toString(), sharedViewModel.getSessionKey())
                }
                is ApiResponse.Loading -> {
                    orderInfoLoading?.visibility = VISIBLE
                }

                is ApiResponse.Error -> {
                    val errorMessage = response.message ?: "An error occurred"
                    showToast(errorMessage)
                }

                is ApiResponse.ErrorLogin -> {
                    val errorMessage = response.data?.message ?: "Login error occurred"
                    orderInfoLoading?.visibility = VISIBLE
                    showToast(errorMessage)
                    orderInfoLoading?.visibility = INVISIBLE
                    // Logs login error to Firebase Analytics
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "Login error")
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, username.toString())
                    bundle.putString(FirebaseAnalytics.Param.CONTENT, errorMessage)
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                }

                is ApiResponse.ErrorSendOrderDate -> {
                    val errorMessage = "An unexpected error occurred"
                    showToast(errorMessage)
                }

                is ApiResponse.NoDataError -> {
                    val errorMessage = "An unexpected error occurred"
                    showToast(errorMessage)
                }

                is ApiResponse.UnknownError -> {
                    val errorMessage = "An unexpected error occurred"
                    showToast(errorMessage)
                }
            }
            clearNotTouchableFlag()
        }
    }


    private fun handleOrderInfoResponse(response: ApiResponse<OrderInfo>?) {
        when (response) {
            is ApiResponse.Success -> {
                //sharedViewModel.setOrderInfo(response)
                val deliveryAddressList = response.data?.deliveryAddresses
                val orderingGroupList = response.data?.orderingGroups
                context?.let { context ->
                    if (orderingGroupList != null) {
                        loginView.insertToDatabase(context, deliveryAddressList, orderingGroupList)
                    }
                }
                val navController = view?.let { findNavController(it) }
                if (navController?.currentDestination?.id != R.id.landingPageFragment) {
                    navController?.navigate(R.id.action_loginFragment_to_landingPageFragment)
                }
            }

            is ApiResponse.Loading -> {
                orderInfoLoading?.visibility = VISIBLE

            }

            is ApiResponse.Error -> TODO()
            is ApiResponse.ErrorLogin -> TODO()
            is ApiResponse.ErrorSendOrderDate -> TODO()
            is ApiResponse.NoDataError -> TODO()
            is ApiResponse.UnknownError -> TODO()
            null -> TODO()
        }

    }

    private fun handleSessionKey(sessionKey: String) {
        sharedViewModel.setSessionKey(sessionKey)
        val sessionKeyRequest = OrderingRequest(sessionKey)
        loginView.getOrderInfo(sessionKeyRequest)
        loginView.orderInfoResponse.observe(viewLifecycleOwner) { orderInfoResponse ->
            handleOrderInfoResponse(orderInfoResponse)
        }
    }

    private fun logLoginSuccess(username: String, sessionKey: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.METHOD, "Login Successful")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, username)
        bundle.putString(FirebaseAnalytics.Param.CONTENT, sessionKey)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }

    private fun showNoInternetToast() {
        Toast.makeText(
            requireContext(),
            "There is no Internet connection!\nPlease check you have a connection.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun clearNotTouchableFlag() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}

