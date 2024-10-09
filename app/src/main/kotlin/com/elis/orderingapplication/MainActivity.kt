package com.elis.orderingapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.util.Log
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
        TooLargeTool.startLogging(this.application);
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("MainActivity", "onSaveInstanceState: Bundle size = ${getBundleSize(outState)} bytes")
        // ... save data to outState ...
    }

    private fun getBundleSize(bundle: Bundle): Int {
        val parcel = Parcel.obtain()
        bundle.writeToParcel(parcel, 0)
        val size = parcel.dataSize()
        parcel.recycle()
        return size
    }

    fun restartActivity(){
        recreate()
    }


}
