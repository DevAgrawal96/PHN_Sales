package com.phntechnolab.sales.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phntechnolab.sales.model.ChangePasswordModel
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.UserDataModel
import com.phntechnolab.sales.model.UserResponse
import com.phntechnolab.sales.repository.UserProfileRepository
import com.phntechnolab.sales.util.NetworkResult
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

    fun changePassword(context: Context, changePasswordModel: ChangePasswordModel) {
        viewModelScope.launch {
            userProfileRepository.changePassword(context, changePasswordModel)
        }
    }
}