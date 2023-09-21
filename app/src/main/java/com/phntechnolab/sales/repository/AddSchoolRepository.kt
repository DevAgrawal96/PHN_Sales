package com.phntechnolab.sales.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.RetrofitApi
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.model.UserResponse
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.NetworkUtils
import okhttp3.RequestBody
import timber.log.Timber
import java.sql.Time
import javax.inject.Inject

class AddSchoolRepository @Inject constructor(
    private val application: Application,
    private val retrofitApi: RetrofitApi
)  {

    private val _addSchoolResponse = MutableLiveData<NetworkResult<CustomResponse>>()

    val addSchoolResponse: LiveData<NetworkResult<CustomResponse>>
        get() = _addSchoolResponse

    private val _updateSchoolResponse = MutableLiveData<NetworkResult<CustomResponse>>()

    val updateSchoolResponse: LiveData<NetworkResult<CustomResponse>>
        get() = _updateSchoolResponse

    suspend fun updateSchoolData(id: String, schoolData: RequestBody){
        if (NetworkUtils.isInternetAvailable(application)) {
            try{
                val result = retrofitApi.updateSchoolData(id, schoolData)
                Timber.e("Result 1")
                Timber.e(Gson().toJson(result.body()))
                Timber.e(result.code().toString())
                result.body()?.status_code = result.code()
                if (result.isSuccessful && result.body() != null) {
                    _updateSchoolResponse.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    _updateSchoolResponse.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse( result.code(), result.errorBody()?.string())
                        )
                    )
                } else {
                    _updateSchoolResponse.postValue(
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

    suspend fun addNewSchool(schoolData: RequestBody){
        if (NetworkUtils.isInternetAvailable(application)) {
            try{
                val result = retrofitApi.addSchool(schoolData)
                Timber.e("Result 1")
                Timber.e(Gson().toJson(result.body()))
                Timber.e(result.code().toString())
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