package com.phntechnolab.sales.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.login.setOnClickListener {
            if(isValid()) {
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
//                            showAllExceptNoInternet(
//                                LoadingModel(isLoading = false, isCheck = true,
//                                    isInternetAvailable = true, retryNow = false)
//                            )
//                            setData(it.data.apps)
                        }

                        404 -> {
//                            hideAllViewExceptNoFound(true)
                        }

                        408 -> {
                            // token expire
//                            viewModel.getTokenIfExpired()
                        }
                    }
                }

                is NetworkResult.Error -> {
                    if (!it.data?.message.isNullOrBlank() && it.data?.message == getString(R.string.no_internet_connection)) {
//                        showAllExceptNoInternet(
//                            LoadingModel(isLoading = false, isCheck = false,
//                                isInternetAvailable = false, retryNow = false)
//                        )
                    } else {
//                        if (!it.data?.message.isNullOrBlank()) {
//                            showAllExceptNoInternet(
//                                LoadingModel(isLoading = false, isCheck = false,
//                                    isInternetAvailable = true, retryNow = false)
//                            )
                            Snackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                "${it.message}",
                                Snackbar.LENGTH_SHORT
                            ).show()
//                        }
                    }
                }

                else -> {

                }
            }
        })
    }

    private fun isValid(): Boolean{
        var isEmailValid = false
        binding.edtEmailId.addTextChangedListener(object : TextValidator(binding.edtEmailId) {

            override fun validate(textView: TextInputEditText?, text: String?) {
                Timber.e("Validate method 1")
                Timber.e(textView?.text.toString())
                Timber.e(text)
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                    isEmailValid =  true
                    textView?.error = null
                } else {
                    isEmailValid =  false
                    textView?.error = "Please enter valid email address"
                }
            }
        })

        return true;
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}