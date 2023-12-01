package com.phntechnolab.sales.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.phntechnolab.sales.api.AuthApi
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.LoginDetails
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.paging.SchoolPagingSource
import com.phntechnolab.sales.repository.HomeRepository
import com.phntechnolab.sales.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repositories: HomeRepository,
    private val authApi: AuthApi,
    private val application: Application
) : ViewModel() {

    val schoolLiveData: LiveData<NetworkResult<List<SchoolData>>>
        get() = repositories.schoolDataLiveData

    val refereshToken: LiveData<NetworkResult<CustomResponse>>
        get() = repositories.refereshToken

    fun getAllSchools() {
        viewModelScope.launch {
            repositories.getSchoolData()
        }
    }

    var _schoolPagingData = MutableLiveData<PagingData<SchoolData>>()
    val schoolPagingData: LiveData<PagingData<SchoolData>>
        get() = _schoolPagingData
//
//    var _schoolPaginationObservableData: Flow<PagingData<SchoolData>>? = null
//    val schoolPaginationObservableData: Flow<PagingData<SchoolData>>? get() = _schoolPaginationObservableData!!
//

//    fun refereshToken() {
//        viewModelScope.launch {
//            repositories.getToken()
//        }
//    }
//
//    fun refereshData() {
//    }
//
//    fun getSchoolList(list : (schoolList : List<SchoolData>) -> Unit) {
//        viewModelScope.launch(Dispatchers.IO) {
//            list(schoolPaginationObservableData?.toList())
//
//        }
//    }

    fun getAllSchoolsPagination(): LiveData<PagingData<SchoolData>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = 15,
                maxSize = 15 + 15 * 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SchoolPagingSource(application = application, authApi)
            }, initialKey = 1
        )
//        _schoolPaginationObservableData = pager.flow
        return pager.liveData.cachedIn(viewModelScope)

//        schoolPaginationDataMutableLiveData.postValue( pager)
    }

    fun setPagingData(pagingData: PagingData<SchoolData>) {
        _schoolPagingData.postValue(pagingData)
    }
}