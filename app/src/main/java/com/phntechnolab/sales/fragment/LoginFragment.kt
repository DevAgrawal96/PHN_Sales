package com.phntechnolab.sales.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.phntechnolab.sales.Modules.DataStoreProvider
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.FragmentLoginBinding
import com.phntechnolab.sales.model.LoginDetails
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.util.DataStoreManager.setToken
import com.phntechnolab.sales.util.DataStoreManager.setUser
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.NetworkUtils
import com.phntechnolab.sales.util.TextValidator
import com.phntechnolab.sales.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var dataStoreProvider: DataStoreProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getToken()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        setBackPressed()
        return binding.root
    }

    private fun setBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.login.setOnClickListener {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(binding.edtEmailId.text.toString())
                    .matches()
            ) {
                val email_id = binding.tilEmailId.helperText
                val password = binding.tilPassword.helperText

                if (email_id == null && password == null) {
                    if (NetworkUtils.isInternetAvailable(it.context)) {
                        val loginDetails = LoginDetails(
                            binding.edtEmailId.text.toString(),
                            password = binding.edtPassword.text.toString()
                        )

//                    hideKeyboard()
//                    disableScreen()
                        viewModel.login(loginDetails, it.context)

                    } else {
                        Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            getString(R.string.no_internet_connection),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

//        viewModel.refereshToken.observe(viewLifecycleOwner){
//            when(it){
//                is NetworkResult.Success ->{
//                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
//                }
//                is NetworkResult.Error ->{
//                    Timber.e(it.toString())
//                }
//                else -> {
//                }
//            }
//
//        }

        viewModel.loginLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Loading -> {
//                    showAllExceptNoInternet(
//                        LoadingModel(isLoading = true, isCheck = false,
//                            isInternetAvailable = true, retryNow = false)
//                    )
                }

                is NetworkResult.Success -> {
                    Timber.d("token-Tasks=${it}")
                    when (it.data?.status_code) {
                        200 -> {

                            lifecycleScope.launch(Dispatchers.IO) {
                                setToken(
                                    requireContext(),
                                    dataStoreProvider,
                                    "authToken",
                                    it.data.access_token.toString()
                                )

                                setUser(
                                    requireContext(),
                                    dataStoreProvider,
                                    "user",
                                    it.data
                                )
                            }.invokeOnCompletion {
                                lifecycleScope.launch(Dispatchers.Main) {

                                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                                }
                            }
                        }
                    }
                }

                is NetworkResult.Error -> {
                    when (it.data?.status_code) {
                        401 -> {
                            Toast.makeText(
                                requireContext(),
                                "unauthorized user",
                                Toast.LENGTH_SHORT
                            ).show()
                            showError()
                        }

                        else -> {
                            Timber.e("Error body else")
                        }
                    }

                }

                else -> {
                }
            }
        })

        addEmailValidation()
        focusListener()
    }

    private fun showError() {
        binding.tilPassword.helperText = getString(R.string.enter_valid_email_and_password)
    }


    private fun focusListener() {
        binding.edtEmailId.setOnFocusChangeListener { v, focused ->
            if (!focused) {
                Timber.e("hide-if")
//                hideSoftKeyboard()
            }
        }
        binding.edtPassword.setOnFocusChangeListener { v, focused ->
            if (!focused) {
                Timber.e("hide-if")
//                hideSoftKeyboard()
            }
        }
    }


    private fun addEmailValidation() {
        binding.edtEmailId.addTextChangedListener(object : TextValidator(binding.edtEmailId) {

            override fun validate(textView: TextInputEditText?, text: String?) {
                Timber.e(textView?.text.toString())
                Timber.e(text)
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                    binding.tilEmailId.helperText = ""
//                    binding.tilPassword.helperText = ""
                } else {
                    binding.tilEmailId.helperText = getString(R.string.enter_valid_email)
                    binding.tilPassword.helperText = ""
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}