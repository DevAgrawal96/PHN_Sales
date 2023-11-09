package com.phntechnolab.sales.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phntechnolab.sales.model.ChangePasswordModel
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.LoginDetails
import com.phntechnolab.sales.model.UserDataModel
import com.phntechnolab.sales.model.UserResponse
import com.phntechnolab.sales.repository.UserProfileRepository
import com.phntechnolab.sales.util.AppEvent
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.isValidName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(private var userProfileRepository: UserProfileRepository) :
    ViewModel() {
    val userProfile: LiveData<NetworkResult<UserDataModel>> =
        userProfileRepository.userProfileLiveData

    val logoutLiveData: LiveData<NetworkResult<CustomResponse>>
        get() = userProfileRepository.logoutLiveData

    val changePasswordLiveData: LiveData<NetworkResult<CustomResponse>>
        get() = userProfileRepository.changePasswordLiveData

    var _changePasswordData: MutableLiveData<ChangePasswordModel?> = MutableLiveData()
    val changePasswordData: LiveData<ChangePasswordModel?>
        get() = _changePasswordData

    private var _toastState = MutableLiveData<AppEvent>()
    val toastState: LiveData<AppEvent> = _toastState

    fun userProfileData() {
        viewModelScope.launch {
            userProfileRepository.userProfileData()
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            userProfileRepository.logout(context)
        }
    }

    fun changePassword() {
        _toastState.postValue(AppEvent.LoadingEvent(true))
        if (isValidName(_changePasswordData.value?.old_password.toString()).toString() != "" &&
            isValidName(_changePasswordData.value?.new_password.toString()).toString() != "" &&
            isValidName(_changePasswordData.value?.confirm_password.toString()).toString() != ""
        ) {

            if (_changePasswordData.value?.new_password.toString() == _changePasswordData.value?.confirm_password.toString()) {
                viewModelScope.launch {
                    userProfileRepository.changePassword(_changePasswordData.value!!)
                }
            } else {
                _toastState.postValue(AppEvent.ToastEvent("The new password and the confirmed password do not match."))
            }
        } else {
            _toastState.postValue(AppEvent.ToastEvent("Please fill all fields!"))
        }

    }
}