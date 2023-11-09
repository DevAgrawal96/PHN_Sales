package com.phntechnolab.sales.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.FragmentChangePasswordBinding
import com.phntechnolab.sales.model.ChangePasswordModel
import com.phntechnolab.sales.util.AppEvent
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.setupUI
import com.phntechnolab.sales.util.toastMsg
import com.phntechnolab.sales.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        setOnBackPressed()
        viewModel._changePasswordData.postValue(ChangePasswordModel())
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    private fun setOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        initializeListener()
        observable()

    }

    private fun observable() {
        viewModel.changePasswordLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    binding.changePasswordButton.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        "Password change successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }

                is NetworkResult.Error -> {
                    hideAndShowProgress(false)
                }

                is NetworkResult.Loading -> {}

                else -> {}
            }
        }

        viewModel.toastState.observe(viewLifecycleOwner) {
            when (it) {
                is AppEvent.ToastEvent -> {
                    toastMsg(it.message)
                    hideAndShowProgress(false)
                }
                is AppEvent.LoadingEvent -> {
                    hideAndShowProgress(true)
                }
            }
        }
    }

    private fun hideAndShowProgress(state: Boolean) {
        if (state) {
            binding.changePasswordButton.isEnabled = false
            binding.progressIndicator.visibility = View.VISIBLE
        } else {
            binding.changePasswordButton.isEnabled = true
            binding.progressIndicator.visibility = View.GONE
        }
    }

    private fun initializeListener() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
//        binding.changePasswordButton.setOnClickListener {
//            if (binding.edtOldPassword.text?.isNotBlank()!! &&
//                binding.edtNewPassword.text?.isNotBlank()!! &&
//                binding.edtConfirmPassword.text?.isNotBlank()!!
//            ) {
//                viewModel.changePassword(
//                    requireContext(),
//                    ChangePasswordModel(
//                        binding.edtConfirmPassword.text.toString(),
//                        binding.edtNewPassword.text.toString(),
//                        binding.edtOldPassword.text.toString()
//                    )
//                )
//                binding.changePasswordButton.isEnabled = false
//                binding.progressIndicator.visibility = View.VISIBLE
//
//            } else {
//                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
//                    .show()
//            }
//
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}