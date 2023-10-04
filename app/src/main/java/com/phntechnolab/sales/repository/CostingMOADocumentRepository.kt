package com.phntechnolab.sales.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.RetrofitApi
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.MOADocumentData
import com.phntechnolab.sales.model.ProposeCostingData
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.NetworkUtils
import javax.inject.Inject

class CostingMOADocumentRepository @Inject constructor(
    private val application: Application,
    private val retrofitApi: RetrofitApi
) {

    private val _proposeCostingDetails = MutableLiveData<NetworkResult<CustomResponse>>()
    val proposeCostingDetails: LiveData<NetworkResult<CustomResponse>>
        get() = _proposeCostingDetails


    private val _moaDocumentDetails = MutableLiveData<NetworkResult<CustomResponse>>()
    val moaDocumentDetails: LiveData<NetworkResult<CustomResponse>>
        get() = _moaDocumentDetails

    suspend fun proposeCostingData(proposeCostingData: ProposeCostingData){
        if (NetworkUtils.isInternetAvailable(application)) {
            try{
                val result = retrofitApi.proposeCostingApi(proposeCostingData)
                result.body()?.status_code = result.code()
                if (result.isSuccessful && result.body() != null) {
                    _proposeCostingDetails.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    _proposeCostingDetails.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse( result.code(), result.errorBody()?.string())
                        )
                    )
                } else {
                    _proposeCostingDetails.postValue(
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

    suspend fun moaDocumentData(moaDocumentData: MOADocumentData){
        if (NetworkUtils.isInternetAvailable(application)) {
            try{
                val result = retrofitApi.moaDocumentApi(moaDocumentData)
                result.body()?.status_code = result.code()
                if (result.isSuccessful && result.body() != null) {
                    _moaDocumentDetails.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    _moaDocumentDetails.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong),
                            CustomResponse( result.code(), result.errorBody()?.string())
                        )
                    )
                } else {
                    _moaDocumentDetails.postValue(
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