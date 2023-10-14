package com.phntechnolab.sales.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.repository.PendingForAprrovalRepository
import com.phntechnolab.sales.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendingForApprovalViewModel @Inject constructor(private val repositories: PendingForAprrovalRepository): ViewModel() {

    val schoolLiveData : LiveData<NetworkResult<List<SchoolData>>>
        get() = repositories.schoolDataLiveData

    fun getAllSchools() {
        viewModelScope.launch {
            repositories.getSchoolData()
        }
    }

}