package com.phntechnolab.sales.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.RetrofitApi
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.model.UserResponse
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.NetworkUtils
import timber.log.Timber
import javax.inject.Inject

class AddSchoolRepository @Inject constructor(
    private val application: Application,
    private val retrofitApi: RetrofitApi
)  {

    private val _addSchoolResponse = MutableLiveData<NetworkResult<CustomResponse>>()

    val addSchoolResponse: LiveData<NetworkResult<CustomResponse>>
        get() = _addSchoolResponse

    suspend fun addNewSchool(schoolData: SchoolData){
        if (NetworkUtils.isInternetAvailable(application)) {
            try{
                val result = retrofitApi.addSchool(schoolData)
                result.body()?.status_code = result.code()
                if (result.isSuccessful && result.body() != null) {
                    _addSchoolResponse.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    _addSchoolResponse.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse( result.code(), result.errorBody()?.string())
                        )
                    )
                } else {
                    _addSchoolResponse.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse( result.code(), application.getString(R.string.something_went_wrong))
                        )
                    )
                }
            }catch (ex: Exception){
                ex.printStackTrace()
            }
        } else {
        }
    }
}