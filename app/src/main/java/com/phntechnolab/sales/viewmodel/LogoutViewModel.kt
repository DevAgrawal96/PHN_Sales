package com.phntechnolab.sales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.repository.LoginRepository
import com.phntechnolab.sales.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private var loginRepository: LoginRepository
) : ViewModel() {
//
//    val logoutLiveData: LiveData<NetworkResult<CustomResponse>> = loginRepository.logoutLiveData
//
//    fun logout(context: Context) {
//        viewModelScope.launch {
//            loginRepository.logout(context)
//        }
//    }
}