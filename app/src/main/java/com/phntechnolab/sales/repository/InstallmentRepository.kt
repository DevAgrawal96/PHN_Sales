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
import com.phntechnolab.sales.model.InstallmentData
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.NetworkUtils
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject

class InstallmentRepository @Inject constructor(
    private val application: Application,
    private val retrofitApi: RetrofitApi
) {
    private val _installmentResponse = MutableLiveData<NetworkResult<CustomResponse>>()

    val installmentResponse: LiveData<NetworkResult<CustomResponse>>
        get() = _installmentResponse

    private val _installmentImageResponse = MutableLiveData<NetworkResult<CustomResponse>>()

    val installmentImageResponse: LiveData<NetworkResult<CustomResponse>>
        get() = _installmentImageResponse

    suspend fun uploadInstallmentData(installmentData: InstallmentData) {
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                Timber.e(Gson().toJson(installmentData))
                val result = retrofitApi.uploadInstallmentData(installmentData)
                result.body()?.status_code = result.code()
                if (result.isSuccessful && result.body() != null) {
                    _installmentResponse.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    Timber.e(result.code().toString()+"add installment data")
                    _installmentResponse.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse(result.code(), result.errorBody()?.string())
                        )
                    )
                } else {
                    Timber.e(result.code().toString())
                    _installmentResponse.postValue(
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
                Timber.e(ex.message)
                _installmentResponse.postValue(
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
            _installmentResponse.postValue(
                NetworkResult.Error(
                    application.getString(R.string.please_connection_message),
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

    suspend fun uploadInstallmentImage(schoolId : String,multiPart: MultipartBody) {
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                Timber.e(Gson().toJson(multiPart))
                val result = retrofitApi.updateInstallmentImage(schoolId, multiPart)
                result.body()?.status_code = result.code()
                if (result.isSuccessful && result.body() != null) {
                    _installmentImageResponse.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    Timber.e(result.code().toString())
                    _installmentImageResponse.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse(result.code(), result.errorBody()?.string())
                        )
                    )
                } else {
                    Timber.e(result.code().toString())
                    _installmentImageResponse.postValue(
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
                Timber.e(ex.message)
                _installmentImageResponse.postValue(
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
            _installmentImageResponse.postValue(
                NetworkResult.Error(
                    application.getString(R.string.please_connection_message),
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
}