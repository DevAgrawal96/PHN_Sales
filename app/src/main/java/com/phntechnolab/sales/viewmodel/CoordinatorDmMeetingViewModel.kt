package com.phntechnolab.sales.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phntechnolab.sales.model.CoordinatorData
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.DMData
import com.phntechnolab.sales.repository.CoordinatoreDmMeetingRepository
import com.phntechnolab.sales.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoordinatorDmMeetingViewModel @Inject constructor(private val repositories: CoordinatoreDmMeetingRepository) : ViewModel() {

    var _coordinatorMeetData: MutableLiveData<CoordinatorData?> = MutableLiveData()
    val coordinatorMeetData: LiveData<CoordinatorData?>
        get() = _coordinatorMeetData

    var _dmMeetData: MutableLiveData<DMData?> = MutableLiveData()
    val dmMeetData: LiveData<DMData?>
        get() = _dmMeetData

    val updateCoordinatorLevelMeetDetails: LiveData<NetworkResult<CustomResponse>>
        get() = repositories.updateCoordinatorLevelMeetDetails

    val updateDMLevelMeetDetails: LiveData<NetworkResult<CustomResponse>>
        get() = repositories.updateDMLevelMeetDetails

    fun updateCoordinatorDetails(){

        viewModelScope.launch {
            repositories.updateCoordinatorData(_coordinatorMeetData.value?: CoordinatorData())
        }
    }

    fun updatedMDetails(){

        viewModelScope.launch {
            repositories.updateDMData(_dmMeetData.value?: DMData())
        }
    }

}