package com.elis.orderingapplication

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.OrderingRequest
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.viewModels.LoginViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import android.widget.ProgressBar
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import com.elis.orderingapplication.adapters.LoginAdapter
import com.elis.orderingapplication.databinding.FragmentLoginBinding
import com.elis.orderingapplication.model.OrderingLoginResponseStruct
import com.elis.orderingapplication.model.OrderingOrderInfoResponseStruct
import com.elis.orderingapplication.pojo2.OrderInfo
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import com.elis.orderingapplication.utils.InternetCheck
import com.elis.orderingapplication.viewModels.LoginViewModelFactory
import com.google.firebase.remoteconfig.FirebaseRemoteConfigClientException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigFetchThrottledException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()
    private lateinit var loginView: LoginViewModel
    lateinit var loginAdapter: LoginAdapter

    private var username: Editable? = null
    private var password: Editable? = null
    private var orderInfoLoading: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        val view = binding.root
        sharedViewModel.setAppVersion(BuildConfig.VERSION_NAME)
        sharedViewModel.setFlavor(BuildConfig.FLAVOR)
        val rep = UserLoginRepository()
        val provider = LoginViewModelFactory(rep)
        val loginViewModel = ViewModelProvider(this, provider)[LoginViewModel::class.java]
        loginView = loginViewModel
        binding.apply { viewModel = loginViewModel }
        binding.apply { paramViewModel = sharedViewModel }
        binding.lifecycleOwner = this
        orderInfoLoading = binding.orderInfoLoading

        fireBaseRemoteConfig()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // sets Today's date for login activity
        binding.date.text = loginView.getDate()
        sharedViewModel.setOrderDate(binding.date.text.toString())
        sharedViewModel.setAppVersion(BuildConfig.VERSION_NAME)
        sharedViewModel.setFlavor(BuildConfig.FLAVOR)
        // sets Flavor banner details for login activity
        setFlavorBanner()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            var isInternetAvailable = checkInternetAvailability()

            if(!isInternetAvailable)
            {
                showAlertDialog()

            }
        }



        with(binding) {
            loginButton.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                    var isInternetAvailable = checkInternetAvailability()
                    //isInternetAvailable = true
                    if (isInternetAvailable) {
                        // Internet is available, proceed with network operations
                        if (checkUsernamePassword()) {
                            // Sets the required username and password to be sent within the Login Api call.
                            requireActivity().window.setFlags(
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            )
                            it.hideKeyboard()
                            // Gets and sets the user entered username and password.
                            val usernameText = username.text.toString()
                            val passwordText = password.text.toString()
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
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Internet Connection")
        builder.setMessage("There is currently no internet connection. Please check your connection and try again.")
        builder.setIcon(R.drawable.outline_error_24)
        builder.setPositiveButton("OK") { dialog, which ->
            // Handle positive button click
            dialog.dismiss()
        }

        builder.setCancelable(true)

        val dialog = builder.create()
        dialog.show()
    }

    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun setFlavorBanner() {
        val flavorBanner = binding.flavorBanner
        // sets banner text
        if (sharedViewModel.flavor.value == "development") {
            flavorBanner.text = resources.getString(R.string.devFlavorText)
        }
        // hides banner if PROD application
        if (sharedViewModel.flavor.value == "production") {
            flavorBanner.isVisible = false
        }
        // sets banner text and banner color
        if (sharedViewModel.flavor.value == "staging") {
            flavorBanner.text = resources.getString(R.string.testFlavorText)
            flavorBanner.run {
                setBackgroundColor(
                    ContextCompat.getColor(
                        context, R.color.elis_orange
                    )
                )
            }
        }
    }

    private fun fireBaseRemoteConfig() {
        // sets the Firebase Remote Config settings
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        // Fetches remote config parameters setup in the Firebase console.
        remoteConfig.fetchAndActivate()
    }

    private fun checkUsernamePassword(): Boolean {

        username = binding.username.text
        password = binding.password.text

        return if (TextUtils.isEmpty(binding.username.text) || TextUtils.isEmpty(binding.password.text)) {
            Toast.makeText(
                activity,
                "Username or Password cannot be empty",
                Toast.LENGTH_LONG
            )
                .show()
            false
        } else true
    }

    private suspend fun checkInternetAvailability(): Boolean {
        return withContext(Dispatchers.IO) {
            val isInternetAvailable = InternetCheck.isInternetAvailable()
            isInternetAvailable
        }
    }

    private fun handleLoginResponse(response: ApiResponse<OrderingLoginResponseStruct>?) {
        if (response != null) {
            when (response) {
                is ApiResponse.Success -> {
                    orderInfoLoading?.visibility = VISIBLE
                    response.data?.sessionKey?.let { sessionKey ->
                        sharedViewModel.setSessionKey(sessionKey)
                        val sessionKeyRequest = OrderingRequest(sessionKey)
                        loginView.getOrderInfo(sessionKeyRequest)
                        loginView.orderInfoResponse.observe(viewLifecycleOwner) { orderInfoResponse ->
                            handleOrderInfoResponse(orderInfoResponse)
                        }
                    }
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
                }

                is ApiResponse.ErrorSendOrderDate -> {
                    val errorMessage = "An unexpected error occurred"
                    showToast(errorMessage)
                }

                is ApiResponse.Loading -> {
                    orderInfoLoading?.visibility = VISIBLE
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
                sharedViewModel.setOrderInfo(response)
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

    private fun showNoInternetToast() {
        Toast.makeText(
            requireContext(),
            "There is no Internet connection!\nPlease check you have a connection.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showGenericErrorToast() {
        Toast.makeText(
            requireContext(),
            "There was an issue loading data. Please try logging in again.",
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

