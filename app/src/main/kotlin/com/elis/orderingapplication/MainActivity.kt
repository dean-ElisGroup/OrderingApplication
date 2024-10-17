package com.elis.orderingapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.MenuHost
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.elis.orderingapplication.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.gu.toolargetool.TooLargeTool

class MainActivity : AppCompatActivity(), MenuHost {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.myNavHostFragment)
        // Setup Toolbar
        supportActionBar?.hide()
        //TooLargeTool.startLogging(this.application);
    }

    fun restartActivity() {
        recreate()
    }

    //Clear the Activity's bundle of the subsidiary fragments' bundles.
    //Effectively clearing all the fragments' bundles, preventing the TransactionTooLargeException error. This should not be used
    //if the actual state of the fragment is needed in memory, for example, keeping unsaved data etc.
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }


}
