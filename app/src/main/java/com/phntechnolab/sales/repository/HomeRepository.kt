package com.phntechnolab.sales.repository

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.AuthApi
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.paging.SchoolPagingSource
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.NetworkUtils
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val application: Application,
    private val authApi: AuthApi
) {

    private val schoolDataMutableLiveData = MutableLiveData<NetworkResult<List<SchoolData>>>()

    val schoolDataLiveData: LiveData<NetworkResult<List<SchoolData>>>
        get() = schoolDataMutableLiveData

    private val _refereshToken = MutableLiveData<NetworkResult<CustomResponse>>()
    val refereshToken: LiveData<NetworkResult<CustomResponse>>
        get() = _refereshToken

    val pager = Pager(
        config = PagingConfig(
            pageSize = 15,
            maxSize = 15 + 15*2,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            SchoolPagingSource(application, authApi)
        }
        , initialKey = 1
    ).liveData

    suspend fun getSchoolData() {
//        getToken()
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                val result = authApi.getAllSchoolData()
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
                val result = authApi.refereshToken()
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

    fun getAllSchoolsPagination(): LiveData<PagingData<SchoolData>> {

        return Pager(
            config = PagingConfig(
                pageSize = 15,
                maxSize = 15 + 15*2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SchoolPagingSource(application, authApi)
            }
            , initialKey = 1
        ).liveData
//        schoolPaginationObservableData = pager.flow
//        schoolPaginationDataMutableLiveData.postValue( pager)
    }
}