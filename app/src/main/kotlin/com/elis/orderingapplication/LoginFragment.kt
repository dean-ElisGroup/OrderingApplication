package com.elis.orderingapplication

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
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.elis.orderingapplication.databinding.FragmentLoginBinding
import com.elis.orderingapplication.interfaces.BaseResponse
import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.OrderingRequest
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.viewModels.LoginViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import android.widget.ProgressBar
import com.elis.orderingapplication.BuildConfig

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val sharedViewModel: ParamsViewModel by activityViewModels()

    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    private var username: Editable? = null
    private var password: Editable? = null
    private var orderInfoRequest = OrderingRequest("")
    private var orderInfoLoading: ProgressBar? = null

    //private val repository by lazy {loginViewModel.loadDatabase()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        val view = binding.root
        sharedViewModel.setAppVersion(BuildConfig.VERSION_NAME)
        sharedViewModel.setFlavor(BuildConfig.FLAVOR)
        binding.apply { viewModel = loginViewModel }
        binding.apply { paramViewModel = sharedViewModel }
        binding.lifecycleOwner = this
        orderInfoLoading = binding.orderInfoLoading
        fireBaseRemoteConfig()
        //loginViewModel.loadDatabase()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // sets Today's date for login activity
        binding.date.text = loginViewModel.getDate()
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
                    requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    orderInfoLoading.visibility = VISIBLE
                    it.hideKeyboard()
                    val login = LoginRequest(username.text.toString(), password.text.toString())
                    // Initiates the Login Api call, passing the above username and password.
                    loginViewModel.getSessionKey(login)
                    binding.lifecycleOwner?.let { it1 ->
                        loginViewModel.login.observe(it1)
                        {
                            when (it) {
                                is BaseResponse.Loading -> {
                                    Toast.makeText(
                                        activity,
                                        "Loading Data",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                is BaseResponse.Success<*> -> {
                                    orderInfoRequest = OrderingRequest(it.data?.sessionKey)
                                    loginViewModel.getOrderingData(orderInfoRequest)
                                    binding.lifecycleOwner?.let { it1 ->
                                        loginViewModel.loginResult.observe(it1) {
                                            when (it) {
                                                is BaseResponse.Loading -> {
                                                    Toast.makeText(
                                                        activity,
                                                        "Loading Data",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }

                                                is BaseResponse.Success<*> -> {
                                                    orderInfoLoading.visibility = INVISIBLE
                                                    findNavController().navigate(R.id.action_loginFragment_to_landingPageFragment)
                                                }

                                                is BaseResponse.ErrorLogin<*> -> {
                                                    orderInfoLoading.visibility = INVISIBLE
                                                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                                }

                                                is BaseResponse.Error -> {
                                                    orderInfoLoading.visibility = INVISIBLE
                                                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                                    Toast.makeText(
                                                        activity,
                                                        it.msg,
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                        }
                                    }
                                }

                                is BaseResponse.ErrorLogin<*> -> {
                                    orderInfoLoading.visibility = INVISIBLE
                                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                    Toast.makeText(
                                        activity,
                                        it.data?.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                    loginViewModel.clearSessionData()
                                }

                                is BaseResponse.Error -> {
                                    orderInfoLoading.visibility = INVISIBLE
                                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                    Toast.makeText(
                                        activity,
                                        it.msg,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun View.hideKeyboard(){
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun setFlavorBanner() {
        val flavorBanner = binding.flavorBanner
        // sets banner text
        if (sharedViewModel.flavor.value == "dev") {
            flavorBanner.text = resources.getString(R.string.devFlavorText)
        }
        // hides banner if PROD application
        if (sharedViewModel.flavor.value == "prod") {
            flavorBanner.isVisible = false
        }
        // sets banner text and banner color
        if (sharedViewModel.flavor.value == "preProd") {
            flavorBanner.text = resources.getString(R.string.testFlavorText)
            flavorBanner.run {
                setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.elis_orange
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

        return if (TextUtils.isEmpty(binding.username.text) ||
            TextUtils.isEmpty(binding.password.text)
        ) {
            Toast.makeText(activity, "Username or Password cannot be empty", Toast.LENGTH_LONG)
                .show()
            false
        } else
            true
    }
}
