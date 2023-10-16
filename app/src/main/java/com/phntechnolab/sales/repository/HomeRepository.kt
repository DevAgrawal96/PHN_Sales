package com.phntechnolab.sales.repository

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.RetrofitApi
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.LoginDetails
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.model.UserResponse
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.NetworkUtils
import timber.log.Timber
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val application: Application,
    private val retrofitApi: RetrofitApi
) {

    private val schoolDataMutableLiveData = MutableLiveData<NetworkResult<List<SchoolData>>>()

    val schoolDataLiveData: LiveData<NetworkResult<List<SchoolData>>>
        get() = schoolDataMutableLiveData

    private val _refereshToken = MutableLiveData<NetworkResult<CustomResponse>>()
    val refereshToken: LiveData<NetworkResult<CustomResponse>>
        get() = _refereshToken

    suspend fun getSchoolData() {
//        getToken()
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                val result = retrofitApi.getAllSchoolData()
                if (result.isSuccessful && result?.body() != null) {

                    schoolDataMutableLiveData.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    Toast.makeText(
                        application,
                        application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                        Toast.LENGTH_LONG
                    ).show()
                    schoolDataMutableLiveData.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            ArrayList()
                        )
                    )
                } else {
                    Toast.makeText(
                        application,
                        application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                        Toast.LENGTH_LONG
                    ).show()
                    schoolDataMutableLiveData.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            ArrayList()
                        )
                    )
                }

            } catch (e: Exception) {
                schoolDataMutableLiveData.postValue(
                    NetworkResult.Error(
                        application.getString(R.string.something_went_wrong),
                        null
                    )
                )
                Toast.makeText(
                    application,
                    application.resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            schoolDataMutableLiveData.postValue(
                NetworkResult.Error(
                    application.resources.getString(R.string.please_connection_message),
                    null
                )
            )
            Toast.makeText(
                application,
                application.resources.getString(R.string.please_connection_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    suspend fun getToken() {
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                val result = retrofitApi.refereshToken()
                if (result.isSuccessful && result?.body() != null) {

                    _refereshToken.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
//                    Toast.makeText(
//                        application,
//                        application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
//                        Toast.LENGTH_LONG
//                    ).show()
//                    schoolDataMutableLiveData.postValue(
//                        NetworkResult.Error(
//                            application.getString(R.string.something_went_wrong),
//                            ArrayList()
//                        )
//                    )
                } else {
//                    Toast.makeText(
//                        application,
//                        application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
//                        Toast.LENGTH_LONG
//                    ).show()
//                    schoolDataMutableLiveData.postValue(
//                        NetworkResult.Error(
//                            application.getString(R.string.something_went_wrong),
//                            ArrayList()
//                        )
//                    )
                }

            } catch (e: Exception) {
//                schoolDataMutableLiveData.postValue(
//                    NetworkResult.Error(
//                        application.getString(R.string.something_went_wrong),
//                        null
//                    )
//                )
//                Toast.makeText(
//                    application,
//                    application.resources.getString(R.string.something_went_wrong),
//                    Toast.LENGTH_SHORT
//                ).show()
            }
        } else {
//            schoolDataMutableLiveData.postValue(
//                NetworkResult.Error(
//                    application.resources.getString(R.string.please_connection_message),
//                    null
//                )
//            )
//            Toast.makeText(
//                application,
//                application.resources.getString(R.string.please_connection_message),
//                Toast.LENGTH_SHORT
//            ).show()
        }
    }
}