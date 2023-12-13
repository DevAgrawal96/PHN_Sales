package com.phntechnolab.sales.repository

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.AuthApi
import com.phntechnolab.sales.api.RetrofitApi
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.NetworkUtils
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val application: Application,
    private val authApi: AuthApi
){
    val schoolDataMutableLiveData = MutableLiveData<NetworkResult<List<SchoolData>>>()

    val schoolDataLiveData: LiveData<NetworkResult<List<SchoolData>>>
        get() = schoolDataMutableLiveData

    suspend fun getSchoolData() {
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                val result = authApi.getAllSchoolData()
                if (result.isSuccessful && result?.body() != null) {

                    schoolDataMutableLiveData.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    Toast.makeText(application, application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
                    schoolDataMutableLiveData.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            ArrayList()
                        )
                    )
                } else {
                    Toast.makeText(application, application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
                    schoolDataMutableLiveData.postValue(
                        NetworkResult.Error(
                            "",
                            ArrayList()
                        )
                    )
                }

            } catch (e: Exception) {
                NetworkResult.Error(
                    application.getString(R.string.something_went_wrong),
                    null
                )
                Toast.makeText(
                    application,
                    application.resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                application,
                application.resources.getString(R.string.please_connection_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}