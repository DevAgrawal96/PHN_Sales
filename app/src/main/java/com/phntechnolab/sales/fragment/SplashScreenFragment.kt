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
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.FragmentSplashScreenBinding
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.viewmodel.HomeViewModel
import com.phntechnolab.sales.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenFragment : Fragment() {
    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()
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
        navigateScreen()
    }


    private fun navigateScreen() {
        viewModel.refereshToken.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
                    }, 3000)
                }

                is NetworkResult.Error -> {
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().navigate(R.id.action_splashScreenFragment_to_loginFragment)
                    }, 3000)
                }

                else -> {
                    Toast.makeText(requireContext(), requireActivity().resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}