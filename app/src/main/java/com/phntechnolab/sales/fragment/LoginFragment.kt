package com.phntechnolab.sales.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.phntechnolab.sales.Modules.DataStoreProvider
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.FragmentLoginBinding
import com.phntechnolab.sales.model.LoginDetails
import com.phntechnolab.sales.util.AppEvent
import com.phntechnolab.sales.util.DataStoreManager.setIsUserLoggedIn
import com.phntechnolab.sales.util.DataStoreManager.setToken
import com.phntechnolab.sales.util.DataStoreManager.setUser
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.hideKeyboard
import com.phntechnolab.sales.util.hideSoftKeyboard
import com.phntechnolab.sales.util.isValidEmail
import com.phntechnolab.sales.util.setupUI
import com.phntechnolab.sales.util.textChange
import com.phntechnolab.sales.util.toastMsg
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        setBackPressed()
        viewModel._loginData.postValue(LoginDetails())
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
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
        setupUI(view)

        observer()

        addEmailValidation()
    }


    private fun observer() {
        viewModel.toastState.observe(viewLifecycleOwner) {
            when (it) {
                is AppEvent.ToastEvent -> {
                    toastMsg(it.message)
                }
                else->{
                }
            }
        }

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
                    if (it.message == requireContext().resources.getString(R.string.something_went_wrong)) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (it.message == requireContext().resources.getString(R.string.please_connection_message)) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.please_connection_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else { }

                    if (it.data?.status_code == 200) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            setToken(
                                requireContext(),
                                dataStoreProvider,
                                "authToken",
                                it.data.access_token.toString()
                            )

                            setIsUserLoggedIn(
                                requireContext(),
                                dataStoreProvider,
                                "isLoggedIn",
                                "true"
                            )

                            setUser(
                                requireContext(),
                                dataStoreProvider,
                                "user",
                                it.data
                            )
                        }.invokeOnCompletion {
                            lifecycleScope.launch(Dispatchers.Main) {

                                requireView().findNavController()
                                    .navigate(R.id.action_loginFragment_to_homeFragment)
                            }
                        }
                    } else { }
                }

                is NetworkResult.Error -> {
                    if (it.data?.status_code == 401) {
                        Toast.makeText(
                            requireContext(),
                            "unauthorized user",
                            Toast.LENGTH_SHORT
                        ).show()
                        showError()
                    } else {
                        Timber.e("unauthorized user Error body else")
                        toastMsg(requireContext().resources.getString(R.string.please_connection_message))
                        Timber.e("${it.data?.status_code}")
                    }
                }

                else -> {}
            }
        })

    }

    private fun showError() {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            getString(R.string.enter_valid_email_and_password),
            Snackbar.LENGTH_SHORT
        ).show()
    }


    private fun addEmailValidation() {
        binding.edtEmailId.textChange { email ->
            binding.tilEmailId.error = if (isValidEmail(
                    email,
                    resources.getString(R.string.enter_valid_email)
                ).toString() == "null"
            ) {
                ""
            } else {
                isValidEmail(email, resources.getString(R.string.enter_valid_email)).toString()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}