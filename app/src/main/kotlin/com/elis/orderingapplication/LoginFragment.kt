package com.elis.orderingapplication

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
import androidx.compose.material3.Snackbar
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.navigation.Navigation.findNavController
import com.elis.orderingapplication.adapters.LoginAdapter
import com.elis.orderingapplication.databinding.FragmentLoginBinding
import com.elis.orderingapplication.model.OrderingLoginResponseStruct
import com.elis.orderingapplication.repositories.UserLoginRepository
import com.elis.orderingapplication.utils.ApiResponse
import com.elis.orderingapplication.viewModels.LoginViewModelFactory

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()

    //private val rep = UserLoginRepository()
    //private val provider = LoginViewModelFactory(rep)
    //private val loginViewModel = ViewModelProvider(this, provider)[LoginViewModel::class.java]
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
        // initiates Firebase remote config options
        with(binding) {
            loginButton.setOnClickListener {
                //view
                if (checkUsernamePassword()) {
                    // Sets the required username and password to be sent within the Login Api call.
                    requireActivity().window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                    //orderInfoLoading.visibility = VISIBLE
                    it.hideKeyboard()
                    // Gets and sets the user entered username and password.
                    val login = LoginRequest(username.text.toString(), password.text.toString())
                    // Initiates the Login Api call, passing the above username and password.
                    loginView.getUserLogin(login)
                    loginView.userLoginResponse.observe(viewLifecycleOwner) { response ->
                        when (response) {
                            is ApiResponse.Success -> {
                                orderInfoLoading.visibility = VISIBLE
                                findNavController(view).navigate(R.id.action_loginFragment_to_landingPageFragment)
                            }
                            is ApiResponse.Error -> {
                                orderInfoLoading.visibility = INVISIBLE
                                response.data?.let { error ->
                                    Log.e("TAG", error.message)
                                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                            else  -> {
                                orderInfoLoading.visibility = INVISIBLE
                                if (response?.data?.message != null) {
                                    response.message .let { errorLogin ->
                                        if (errorLogin != null) {
                                            Log.e("TAG", errorLogin)
                                        }
                                        Toast.makeText(requireContext(), errorLogin, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }


                        }
                    }
                }
            }
        }
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
        }

