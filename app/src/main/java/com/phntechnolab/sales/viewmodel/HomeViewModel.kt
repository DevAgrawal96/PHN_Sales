package com.phntechnolab.sales.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phntechnolab.sales.model.LoginDetails
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.repository.HomeRepository
import com.phntechnolab.sales.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repositories: HomeRepository): ViewModel() {

    val schoolLiveData : LiveData<NetworkResult<List<SchoolData>>>
        get() = repositories.schoolDataLiveData

    fun getAllSchools() {
        viewModelScope.launch {
            repositories.getSchoolData()
        }
    }
}