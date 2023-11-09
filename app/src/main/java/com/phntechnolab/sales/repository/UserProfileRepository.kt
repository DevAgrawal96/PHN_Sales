package com.phntechnolab.sales.repository

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.AuthApi
import com.phntechnolab.sales.api.RetrofitApi
import com.phntechnolab.sales.model.ChangePasswordModel
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.UserDataModel
import com.phntechnolab.sales.model.UserResponse
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.NetworkUtils
import javax.inject.Inject

class UserProfileRepository @Inject constructor(
    private val application: Application,
    private val authApi: AuthApi
) {

    private var userProfileMutableLiveData = MutableLiveData<NetworkResult<UserDataModel>>()
    val userProfileLiveData: LiveData<NetworkResult<UserDataModel>> get() = userProfileMutableLiveData

    private var _changePasswordMutableLiveData = MutableLiveData<NetworkResult<CustomResponse>>()
    val changePasswordLiveData: LiveData<NetworkResult<CustomResponse>> get() = _changePasswordMutableLiveData

    private val logoutMutableLiveData = MutableLiveData<NetworkResult<CustomResponse>>()

    val logoutLiveData: LiveData<NetworkResult<CustomResponse>>
        get() = logoutMutableLiveData

    suspend fun logout(context: Context) {
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                val result = authApi.logout()
                result.body()?.status_code = result.code()
                if (result.isSuccessful && result.body() != null) {
                    logoutMutableLiveData.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
//                    Toast.makeText(application, application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
                    logoutMutableLiveData.postValue(
                        NetworkResult.Error(application.getString(R.string.something_went_wrong))
                    )
                } else {
//                    Toast.makeText(application, application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
                    logoutMutableLiveData.postValue(
                        NetworkResult.Error(application.getString(R.string.something_went_wrong))
                    )
                }

            } catch (e: Exception) {
//                Toast.makeText(application, application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
                logoutMutableLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.something_went_wrong))
                )
            }
        } else {
//            Toast.makeText(application, application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
            logoutMutableLiveData.postValue(
                NetworkResult.Error(application.getString(R.string.no_internet_connection),null)
            )
        }
    }

    suspend fun changePassword(changePasswordModel: ChangePasswordModel) {
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                val result = authApi.changePassword(changePasswordModel)
                result.body()?.status_code = result.code()
                if (result.isSuccessful && result.body() != null) {
                    _changePasswordMutableLiveData.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
//                    Toast.makeText(application, application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
                    _changePasswordMutableLiveData.postValue(
                        NetworkResult.Error(application.getString(R.string.something_went_wrong))
                    )
                } else {
//                    Toast.makeText(application, application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
                    _changePasswordMutableLiveData.postValue(
                        NetworkResult.Error(application.getString(R.string.something_went_wrong))
                    )
                }

            } catch (e: Exception) {
//                Toast.makeText(application, application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
                _changePasswordMutableLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.something_went_wrong))
                )
            }
        } else {
//            Toast.makeText(application, application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
            _changePasswordMutableLiveData.postValue(
                NetworkResult.Error(application.getString(R.string.something_went_wrong))
            )
        }
    }

    suspend fun userProfileData() {
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                val result = authApi.getUserProfileData()
                if (result.isSuccessful && result.body() != null) {
                    userProfileMutableLiveData.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
//                    Toast.makeText(application, application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
                    userProfileMutableLiveData.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            null
                        )
                    )
                } else {
//                    Toast.makeText(application, application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
                    userProfileMutableLiveData.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            null
                        )
                    )
                }
            } catch (e: Exception) {
//                Toast.makeText(application, application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
                userProfileMutableLiveData.postValue(
                    NetworkResult.Error(
                        application.resources.getString(R.string.something_went_wrong),
                        null
                    )
                )
            }
        } else {
//            Toast.makeText(application, application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
            userProfileMutableLiveData.postValue(
                NetworkResult.Error(
                    application.resources.getString(R.string.no_internet_connection),
                    null
                )
            )
        }
    }
}