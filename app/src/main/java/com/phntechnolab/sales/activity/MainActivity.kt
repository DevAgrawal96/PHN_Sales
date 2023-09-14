package com.phntechnolab.sales.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        initializeListener()
        hideBottomNavAndToolbar()
    }

    private fun splashFlag() {
        installSplashScreen()
            .setKeepOnScreenCondition { keepSplashOnScreen }
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            keepSplashOnScreen = false
        },3000)

        _binding = ActivityMainBinding.inflate(layoutInflater)
    }

    private fun initializeListener() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }

                else -> {
                    item.isCheckable = false
                    false
                }
            }
        }
    }

    private fun hideBottomNavAndToolbar() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.include.toolbar.visibility = View.VISIBLE
                }

                else -> {
                    binding.bottomNavigation.visibility = View.GONE
                    binding.include.toolbar.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}