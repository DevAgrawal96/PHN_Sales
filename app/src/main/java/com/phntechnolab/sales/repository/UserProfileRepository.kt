package com.phntechnolab.sales.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.RetrofitApi
import com.phntechnolab.sales.model.UserDataModel
import com.phntechnolab.sales.model.UserResponse
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.NetworkUtils
import javax.inject.Inject

class UserProfileRepository @Inject constructor(
    private val application: Application,
    private val retrofitApi: RetrofitApi
) {

    private var userProfileMutableLiveData = MutableLiveData<NetworkResult<UserDataModel>>()
    val userProfileLiveData: LiveData<NetworkResult<UserDataModel>> get() = userProfileMutableLiveData

    suspend fun userProfileData() {
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                val result = retrofitApi.getUserProfileData()
                if (result.isSuccessful && result.body() != null) {
                    userProfileMutableLiveData.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    userProfileMutableLiveData.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            null
                        )
                    )
                } else {
                    userProfileMutableLiveData.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            null
                        )
                    )
                }
            } catch (e: Exception) {
                userProfileMutableLiveData.postValue(
                    NetworkResult.Error(
                        "",
                        null
                    )
                )
            }
        } else {
            userProfileMutableLiveData.postValue(
                NetworkResult.Error(
                    "",
                    null
                )
            )
        }
    }
}