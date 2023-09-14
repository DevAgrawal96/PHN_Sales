package com.phntechnolab.sales.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.RetrofitApi
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

    suspend fun getSchoolData() {
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                Timber.e("APi Call Start")
//                val result = retrofitApi.getAllSchoolData()
                val result = retrofitApi.getAllSchoolData()
                if (result.isSuccessful && result?.body() != null) {
                    Timber.e("api call end")
                    Timber.e(result.toString())
                    schoolDataMutableLiveData.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    Timber.e("api call end error body null")

                    schoolDataMutableLiveData.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            ArrayList()
                        )
                    )
                } else {
                    Timber.e("api call end else wala part")
                    schoolDataMutableLiveData.postValue(
                        NetworkResult.Error(
                            "",
                            ArrayList()
                        )
                    )
                }

            } catch (e: Exception) {
                Timber.d("Exception in SCReg${e.message}")
                schoolDataMutableLiveData.postValue(
                    NetworkResult.Error(
                        "",
                        ArrayList()
                    )
                )
            }
        } else {
            Timber.e("else wala ke bhi else")
            schoolDataMutableLiveData.postValue(
                NetworkResult.Error(
                    "",
                    ArrayList()
                )
            )
        }
    }
}