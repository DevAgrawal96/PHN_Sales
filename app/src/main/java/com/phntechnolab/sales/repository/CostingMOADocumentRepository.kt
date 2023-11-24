package com.phntechnolab.sales.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.AuthApi
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.MOADocumentData
import com.phntechnolab.sales.model.ProposeCostingData
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.NetworkUtils
import okhttp3.MultipartBody
import javax.inject.Inject

class CostingMOADocumentRepository @Inject constructor(
    private val application: Application,
    private val authApi: AuthApi
) {

    private val _proposeCostingDetails = MutableLiveData<NetworkResult<CustomResponse>>()
    val proposeCostingDetails: LiveData<NetworkResult<CustomResponse>>
        get() = _proposeCostingDetails


    private val _moaDocumentDetails = MutableLiveData<NetworkResult<CustomResponse>>()
    val moaDocumentDetails: LiveData<NetworkResult<CustomResponse>>
        get() = _moaDocumentDetails

    private val _moaDocumentFile = MutableLiveData<NetworkResult<CustomResponse>>()
    val moaDocumentFile: LiveData<NetworkResult<CustomResponse>>
        get() = _moaDocumentFile

    suspend fun proposeCostingData(proposeCostingData: ProposeCostingData) {
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                val result = authApi.proposeCostingApi(proposeCostingData)
                result.body()?.status_code = result.code()
                if (result.isSuccessful && result.body() != null) {
                    _proposeCostingDetails.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    Toast.makeText(
                        application,
                        application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                        Toast.LENGTH_LONG
                    ).show()
                    _proposeCostingDetails.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse(result.code(), result.errorBody()?.string())
                        )
                    )
                } else {
                    Toast.makeText(
                        application,
                        application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                        Toast.LENGTH_LONG
                    ).show()
                    _proposeCostingDetails.postValue(
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

    suspend fun moaDocumentData(moaDocumentData: MOADocumentData) {
        Log.e("MOA DOCUMENT DATA", Gson().toJson(moaDocumentData))
        if (NetworkUtils.isInternetAvailable(application)) {
            try {

                val result = authApi.moaDocumentData(moaDocumentData)

                result.body()?.status_code = result.code()

                if (result.isSuccessful && result.body() != null) {

                    _moaDocumentDetails.postValue(NetworkResult.Success(result.body()))

                } else if (result.errorBody() != null) {

                    _moaDocumentDetails.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse(result.code(), result.errorBody()?.string())
                        )
                    )

                } else {

                    _moaDocumentDetails.postValue(
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
                _moaDocumentDetails.postValue(
                    NetworkResult.Error(
                        application.getString(R.string.something_went_wrong),
                        CustomResponse(
                            null,
                            application.getString(R.string.something_went_wrong)
                        )
                    )
                )

            }
        } else {
            _moaDocumentDetails.postValue(
                NetworkResult.Error(
                    application.getString(R.string.please_connection_message),
                    CustomResponse(
                        null,
                        application.getString(R.string.please_connection_message)
                    )
                )
            )
        }
    }

    suspend fun moaDocumentFile(schoolId: String, moaDocumentData: MultipartBody) {
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                val result = authApi.moaDocumentFileUpload(schoolId, moaDocumentData)

                result.body()?.status_code = result.code()

                if (result.isSuccessful && result.body() != null) {

                    _moaDocumentFile.postValue(NetworkResult.Success(result.body()))

                } else if (result.errorBody() != null) {
                    _moaDocumentFile.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse(result.code(), result.errorBody()?.string())
                        )
                    )

                } else {
                    _moaDocumentFile.postValue(
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
                _moaDocumentFile.postValue(
                    NetworkResult.Error(
                        application.getString(R.string.something_went_wrong),
                        CustomResponse(
                            null,
                            application.getString(R.string.something_went_wrong)
                        )
                    )
                )
            }
        } else {
            _moaDocumentFile.postValue(
                NetworkResult.Error(
                    application.getString(R.string.please_connection_message),
                    CustomResponse(
                        null,
                        application.getString(R.string.please_connection_message)
                    )
                )
            )
        }
    }
}