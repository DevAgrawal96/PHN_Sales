package com.phntechnolab.sales

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.phntechnolab.sales.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var keepSplashOnScreen = true
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashFlag()
        setContentView(binding.root)
        supportActionBar?.hide()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun splashFlag() {
        installSplashScreen()
            .setKeepOnScreenCondition { keepSplashOnScreen }
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            keepSplashOnScreen = false
        },3000)

        _binding = ActivityMainBinding.inflate(layoutInflater)

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}