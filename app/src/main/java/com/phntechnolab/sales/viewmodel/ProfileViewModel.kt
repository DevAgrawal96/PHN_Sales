package com.phntechnolab.sales.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phntechnolab.sales.model.UserDataModel
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

    fun userProfileData() {
        viewModelScope.launch {
            userProfileRepository.userProfileData()
        }
    }
}