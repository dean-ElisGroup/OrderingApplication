package com.elis.orderingapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.elis.orderingapplication.databinding.ActivityMainBinding
import com.elis.orderingapplication.viewModels.OrderingGroupViewModel
import com.elis.orderingapplication.viewModels.ParamsViewModel
import com.elis.orderingapplication.viewModels.SharedViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sharedViewModel: ParamsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        installSplashScreen()
        navController = findNavController(R.id.myNavHostFragment)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.hide()

        setupActionBarWithNavController(this, navController)
        supportFragmentManager.findFragmentById(R.id.myNavHostFragment)

    }
}
