package com.phntechnolab.sales.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.phntechnolab.sales.DataStoreProvider
import com.phntechnolab.sales.activity.MainActivity
import com.phntechnolab.sales.databinding.FragmentHomeBinding
import com.phntechnolab.sales.viewmodel.HomeViewModel
import javax.inject.Inject

class HomeFragment: Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel by viewModels<HomeViewModel>()

    @Inject
    lateinit var dataStoreProvider: DataStoreProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setOnBackPressed()
        return binding.root
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

//    private fun setActionBar() {
//        (requireActivity() as MainActivity).setSupportActionBar(binding.topAppBar)
//    }
    private fun setOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }
}