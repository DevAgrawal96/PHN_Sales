package com.phntechnolab.sales.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phntechnolab.sales.R
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.LoginDetails
import com.phntechnolab.sales.model.ProposeCostingData
import com.phntechnolab.sales.model.UserResponse
import com.phntechnolab.sales.repository.LoginRepository
import com.phntechnolab.sales.util.AppEvent
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.isValidEmail
import com.phntechnolab.sales.util.isValidName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repositories: LoginRepository) : ViewModel() {

    val loginLiveData: LiveData<NetworkResult<UserResponse>>
        get() = repositories.loginLiveData

    val refereshToken: LiveData<NetworkResult<CustomResponse>>
        get() = repositories.refereshToken

    var _loginData: MutableLiveData<LoginDetails?> = MutableLiveData()
    val loginData: LiveData<LoginDetails?>
        get() = _loginData

    private var _toastState = MutableLiveData<AppEvent>()
    val toastState: LiveData<AppEvent> = _toastState

    fun login() {
        if (isValidEmail(_loginData.value?.email.toString()).toString() != ""
            && isValidName(_loginData.value?.password.toString()) != ""
        ) {
            viewModelScope.launch {
                repositories.login(_loginData.value!!)
            }
        } else {
            _toastState.postValue(AppEvent.ToastEvent("Please enter email and password!"))
        }
    }


    fun getToken() {
        viewModelScope.launch {
            repositories.refereshToken()
        }
    }
}