package com.phntechnolab.sales.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phntechnolab.sales.model.LoginDetails
import com.phntechnolab.sales.model.UserResponse
import com.phntechnolab.sales.repository.LoginRepository
import com.phntechnolab.sales.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repositories: LoginRepository): ViewModel() {


    val loginLiveData : LiveData<NetworkResult<UserResponse>>
        get() = repositories.loginLiveData

    fun login(login: LoginDetails, context: Context) {
        viewModelScope.launch {
            repositories.login(login, context)
        }
    }
}