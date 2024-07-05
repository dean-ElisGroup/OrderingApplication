package com.elis.orderingapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.elis.orderingapplication.database.OrderInfoDatabase
import com.elis.orderingapplication.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    //lateinit var analytics: FirebaseAnalytics

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

        //analytics = FirebaseAnalytics.getInstance(this)
        //analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, null)
    }
}
