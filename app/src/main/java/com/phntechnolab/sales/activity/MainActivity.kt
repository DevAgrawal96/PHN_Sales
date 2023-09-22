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

    //    private var keepSplashOnScreen = true
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        splashFlag()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        initializeListener()
        hideBottomNavAndToolbar()
    }

//    private fun splashFlag() {
//        installSplashScreen()
//            .setKeepOnScreenCondition { keepSplashOnScreen }
//        android.os.Handler(Looper.getMainLooper()).postDelayed({
//            keepSplashOnScreen = false
//        },3000)
//
//        _binding = ActivityMainBinding.inflate(layoutInflater)
//    }

    private fun initializeListener() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    item.isCheckable = true
                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.menu_profile -> {
                    item.isCheckable = true
                    navController.navigate(R.id.profileFragment)
                    true
                }

                R.id.menu_pending -> {
                    item.isCheckable = true
                    navController.navigate(R.id.pendingFragment)
                    true
                }

                R.id.menu_meeting -> {
                    item.isCheckable = true
                    navController.navigate(R.id.meetingFragment)
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
                    binding.bottomNavigation.menu.findItem(R.id.menu_home).isChecked = true
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.customElevation.visibility = View.VISIBLE
                    //binding.include.toolbar.visibility = View.VISIBLE
                }

                R.id.profileFragment -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.customElevation.visibility = View.VISIBLE
                    //binding.include.toolbar.visibility = View.VISIBLE
                }

                R.id.pendingFragment -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.customElevation.visibility = View.VISIBLE
                    //binding.include.toolbar.visibility = View.VISIBLE
                }

                R.id.meetingFragment -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.customElevation.visibility = View.VISIBLE
                    //binding.include.toolbar.visibility = View.VISIBLE
                }

                else -> {
                    binding.bottomNavigation.visibility = View.GONE
                    binding.customElevation.visibility = View.GONE
                    //binding.include.toolbar.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}