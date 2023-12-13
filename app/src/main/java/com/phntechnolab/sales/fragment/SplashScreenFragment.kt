package com.phntechnolab.sales.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.phntechnolab.sales.Modules.DataStoreProvider
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.FragmentSplashScreenBinding
import com.phntechnolab.sales.util.DataStoreManager
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.viewmodel.HomeViewModel
import com.phntechnolab.sales.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenFragment : Fragment() {
    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var dataStoreProvider: DataStoreProvider

    var isLoggedIn = "false"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getToken()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isLoggedIn = runBlocking {
            DataStoreManager.getIsUserLoggedIn(
                requireContext(),
                dataStoreProvider,
                "isLoggedIn"
            ).toString()
        }

        val destinationId =
            if (isLoggedIn == "true") R.id.action_splashScreenFragment_to_homeFragment
            else R.id.action_splashScreenFragment_to_loginFragment

        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(destinationId)
        }, 3000)

        initializeListener()
    }

    private fun initializeListener() {
        binding.retryBtn.setOnClickListener {
            viewModel.getToken()
            binding.retryBtn.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}