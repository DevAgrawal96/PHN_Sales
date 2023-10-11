package com.phntechnolab.sales.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.RetrofitApi
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.NetworkUtils
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject

class AddSchoolRepository @Inject constructor(
    private val application: Application,
    private val retrofitApi: RetrofitApi
) {

    private val _addSchoolResponse = MutableLiveData<NetworkResult<CustomResponse>>()

    val addSchoolResponse: LiveData<NetworkResult<CustomResponse>>
        get() = _addSchoolResponse

    private val _updateSchoolResponse = MutableLiveData<NetworkResult<CustomResponse>>()

    val updateSchoolResponse: LiveData<NetworkResult<CustomResponse>>
        get() = _updateSchoolResponse

    private val _imageUploadResponse = MutableLiveData<NetworkResult<CustomResponse>>()

    val imageUploadResponse: LiveData<NetworkResult<CustomResponse>>
        get() = _imageUploadResponse

    suspend fun updateSchoolData(id: String, schoolData: SchoolData) {
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                Log.e("Multipart body data", Gson().toJson(schoolData))
                Log.e("Multipart body data", id)
                val result = retrofitApi.updateSchoolData(id, schoolData)
                Timber.e("Result 1 update")
                Timber.e(id.toString())
                Timber.e(Gson().toJson(schoolData))
                Timber.e(Gson().toJson(result.body()))
                Timber.e(result.code().toString())
                result.body()?.status_code = result.code()
                if (result.isSuccessful && result.body() != null) {
                    _updateSchoolResponse.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    _updateSchoolResponse.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse(result.code(), result.errorBody()?.string())
                        )
                    )
                } else {
                    _updateSchoolResponse.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse(
                                result.code(),
                                application.getString(R.string.something_went_wrong)
                            )
                        )
                    )
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
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

    suspend fun addNewSchool(schoolData: MultipartBody) {
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                val result = retrofitApi.addSchool(schoolData)
                Timber.e("Result 1 add")
                Timber.e(Gson().toJson(schoolData))
                Timber.e(Gson().toJson(result.body()))
                Timber.e(result.code().toString())
                result.body()?.status_code = result.code()
                if (result.isSuccessful && result.body() != null) {
                    _addSchoolResponse.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    _addSchoolResponse.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse(result.code(), result.errorBody()?.string())
                        )
                    )
                } else {
                    _addSchoolResponse.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse(
                                result.code(),
                                application.getString(R.string.something_went_wrong)
                            )
                        )
                    )
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
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

    suspend fun uploadImage(id: Int, imageData: MultipartBody) {
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                Timber.e("School image upload start")
                val result = retrofitApi.updateSchoolImage(id, imageData)
                Timber.e("School image upload end")
//                Timber.e(Gson().toJson(result))
                result.body()?.status_code = result.code()
                if (result.isSuccessful && result.body() != null) {
                    _imageUploadResponse.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    _imageUploadResponse.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse(result.code(), result.errorBody()?.string())
                        )
                    )
                } else {
                    _imageUploadResponse.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse(
                                result.code(),
                                application.getString(R.string.something_went_wrong)
                            )
                        )
                    )
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
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