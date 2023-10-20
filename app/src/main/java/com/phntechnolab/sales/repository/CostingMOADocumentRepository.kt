package com.phntechnolab.sales.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.AuthApi
import com.phntechnolab.sales.api.RetrofitApi
import com.phntechnolab.sales.model.CustomResponse
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

    suspend fun moaDocumentData(moaDocumentData: MultipartBody) {
        Log.e("MOA DOCUMENT DATA", Gson().toJson(moaDocumentData))
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                val result = authApi.moaDocumentApi(moaDocumentData)
                result.body()?.status_code = result.code()
                if (result.isSuccessful && result.body() != null) {
                    _moaDocumentDetails.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    Toast.makeText(
                        application,
                        application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                        Toast.LENGTH_LONG
                    ).show()
                    _moaDocumentDetails.postValue(
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