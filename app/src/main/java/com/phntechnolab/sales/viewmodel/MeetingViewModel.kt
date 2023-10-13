package com.phntechnolab.sales.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    val todayMeetingMutableLiveData : LiveData<List<SchoolData>>
        get() = repositories.todayMeetingMutableLiveData

    val tomorrowMeetingMutableLiveData : LiveData<List<SchoolData>>
        get() = repositories.tomorrowMeetingMutableLiveData

    val upcomingMeetingMutableLiveData : LiveData<List<SchoolData>>
        get() = repositories.upcomingMeetingMutableLiveData

    fun getAllSchools() {
        viewModelScope.launch {
            repositories.getSchoolData()
        }
    }

    fun todayMeetingData(){
        repositories.todayMeetingDataSetup()
//        repositories.tomorrowMeetingDataSetup()
//        repositories.upcomingMeetingDataSetup()
    }
}