package com.phntechnolab.sales.repository

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.AuthApi
import com.phntechnolab.sales.api.RetrofitApi
import com.phntechnolab.sales.model.CoordinatorData
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.DMData
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.NetworkUtils
import javax.inject.Inject

class CoordinatoreDmMeetingRepository @Inject constructor(
    private val application: Application,
    private val authApi: AuthApi
) {

    private val _updateCoordinatorLevelMeetDetails =
        MutableLiveData<NetworkResult<CustomResponse>>()
    val updateCoordinatorLevelMeetDetails: LiveData<NetworkResult<CustomResponse>>
        get() = _updateCoordinatorLevelMeetDetails


    private val _updateDMLevelMeetDetails = MutableLiveData<NetworkResult<CustomResponse>>()
    val updateDMLevelMeetDetails: LiveData<NetworkResult<CustomResponse>>
        get() = _updateDMLevelMeetDetails


    suspend fun updateCoordinatorData(coordinatorData: CoordinatorData) {
        if (NetworkUtils.isInternetAvailable(application)) {
            try{
                val result = authApi.updateCoordinaterMeet(coordinatorData)
                result.body()?.status_code = result.code()
                if (result.isSuccessful && result.body() != null) {
                    _updateCoordinatorLevelMeetDetails.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    _updateCoordinatorLevelMeetDetails.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse(result.code(), result.errorBody()?.string())
                        )
                    )
                } else {
                    _updateCoordinatorLevelMeetDetails.postValue(
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
                _updateCoordinatorLevelMeetDetails.postValue(
                    NetworkResult.Error(
                        application.getString(R.string.something_went_wrong),
                        null
                    )
                )
            }
        } else {
            _updateCoordinatorLevelMeetDetails.postValue(
                NetworkResult.Error(
                    application.resources.getString(R.string.please_connection_message),
                    null
                )
            )
        }
    }

    suspend fun updateDMData(dmData: DMData, context: Context) {
        if (NetworkUtils.isInternetAvailable(application)) {
            try{
                val result = authApi.updateDMMeet(dmData)
                result.body()?.status_code = result.code()
                if (result.isSuccessful && result.body() != null) {
                    _updateDMLevelMeetDetails.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    _updateDMLevelMeetDetails.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse(result.code(), result.errorBody()?.string())
                        )
                    )
                } else {
                    _updateDMLevelMeetDetails.postValue(
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
                _updateDMLevelMeetDetails.postValue(
                    NetworkResult.Error(
                        application.getString(R.string.something_went_wrong),
                        null
                    )
                )
            }
        } else {
            _updateCoordinatorLevelMeetDetails.postValue(
                NetworkResult.Error(
                    application.resources.getString(R.string.please_connection_message),
                    null
                )
            )
        }
    }
}