package com.phntechnolab.sales.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phntechnolab.sales.model.MeetingData
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.repository.MeetingRepository
import com.phntechnolab.sales.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeetingViewModel @Inject constructor(private val repositories: MeetingRepository): ViewModel() {

    val schoolLiveData : LiveData<NetworkResult<List<SchoolData>>>
        get() = repositories.allSchoolMutableLiveData
    val meetingsData : LiveData<List<MeetingData>>
        get() = repositories.meetingsData

    fun getAllSchools() {
        viewModelScope.launch {
            repositories.getSchoolData()
        }
    }

    fun segregateData(){
        repositories.segregateData()
    }
}