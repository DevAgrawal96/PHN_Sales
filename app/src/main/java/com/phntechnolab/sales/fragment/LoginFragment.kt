package com.phntechnolab.sales.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.phntechnolab.sales.model.LoginDetails
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.NetworkUtils
import com.phntechnolab.sales.util.disableScreen
import com.phntechnolab.sales.util.hideKeyboard
import com.phntechnolab.sales.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.LoginFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment: Fragment() {
    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.getOtp.setOnClickListener {
            val email_id = binding.tilMono.helperText
            val password = binding.passwordTitle.helperText

            if(email_id == null && password == null){
                if (NetworkUtils.isInternetAvailable(it.context)){
                    val loginDetails = LoginDetails(binding.edtmono.text.toString(), password = binding.passwordText.text.toString())

//                    hideKeyboard()
//                    disableScreen()
                    viewModel.login(loginDetails, it.context)
                }else{
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), getString(R.string.no_internet_connection), Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.loginLiveData.observe(viewLifecycleOwner, Observer {
            when(it){
                is NetworkResult.Loading ->{
//                    showAllExceptNoInternet(
//                        LoadingModel(isLoading = true, isCheck = false,
//                            isInternetAvailable = true, retryNow = false)
//                    )
                }
                is NetworkResult.Success ->{
                    Timber.d("token-Tasks=${it}")
                    when(it.data?.status_code){
                        200 ->{
                            Timber.d(Gson().toJson(it.data))
//                            showAllExceptNoInternet(
//                                LoadingModel(isLoading = false, isCheck = true,
//                                    isInternetAvailable = true, retryNow = false)
//                            )
//                            setData(it.data.apps)
                        }
                        404 ->{
//                            hideAllViewExceptNoFound(true)
                        }
                        408 ->{
                            // token expire
//                            viewModel.getTokenIfExpired()
                        }
                    }
                }
                is NetworkResult.Error ->{
                    if (!it.data?.message.isNullOrBlank() && it.data?.message == getString(R.string.no_internet_connection)){
//                        showAllExceptNoInternet(
//                            LoadingModel(isLoading = false, isCheck = false,
//                                isInternetAvailable = false, retryNow = false)
//                        )
                    }else{
                        if (!it.data?.message.isNullOrBlank()){
//                            showAllExceptNoInternet(
//                                LoadingModel(isLoading = false, isCheck = false,
//                                    isInternetAvailable = true, retryNow = false)
//                            )
                            Snackbar.make(requireActivity().findViewById(android.R.id.content), "${it.data?.message}", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
                else ->{

                }
            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}