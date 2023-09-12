package com.phntechnolab.sales.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phntechnolab.sales.Modules.DataStoreProvider
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.RetrofitApi
import com.phntechnolab.sales.model.LoginDetails
import com.phntechnolab.sales.model.UserResponse
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.NetworkUtils
import timber.log.Timber
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val application: Application,
    private val retrofitApi: RetrofitApi
) {

    @Inject
    lateinit var dataStoreProvider: DataStoreProvider

    private val loginMutableLiveData = MutableLiveData<NetworkResult<UserResponse>>()

    val loginLiveData: LiveData<NetworkResult<UserResponse>>
        get() = loginMutableLiveData

    suspend fun login(loginDetails: LoginDetails, context: Context) {
        Log.d("Timber 10", loginDetails.toString())
//        loginMutableLiveData.postValue(NetworkResult.Loading())
        if (NetworkUtils.isInternetAvailable(application)) {
            Log.d("Timber 11", loginDetails.toString())
            try {
                val result = retrofitApi.getDetails()
                Log.d("TImber 1", result.toString())
                Log.d("TIMber 2", loginDetails.toString())
                if (result.isSuccessful && result?.body() != null) {
                    Log.e("RESULT DAAAA", result.body().toString())
                    result.body()?.status_code = result.code()
                    Log.e("RESULT DAAAA", result.body().toString())
                    Log.e("RESULT DAAAA2", result.code().toString())
                    loginMutableLiveData.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    loginMutableLiveData.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            UserResponse(null, null, null, null, null, null)
                        )
                    )
                } else {
                    loginMutableLiveData.postValue(
                        NetworkResult.Error(
                            "",
                            UserResponse(
                                null,
                                null,
                                null,
                                null,
                                message = application.getString(R.string.something_went_wrong),
                                null
                            )
                        )
                    )
                }

            } catch (e: Exception) {
                Timber.d("Exception in SCReg${e.message}")
                loginMutableLiveData.postValue(
                    NetworkResult.Error(
                        "",
                        UserResponse(null, null, null, null, message = e.message, null)
                    )
                )
            }
        } else {
            loginMutableLiveData.postValue(
                NetworkResult.Error(
                    "",
                    UserResponse(
                        null,
                        null,
                        null,
                        null,
                        message = application.getString(R.string.no_internet_connection),
                        null
                    )
                )
            )
        }
    }
}