package com.phntechnolab.sales.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.repository.AddSchoolRepository
import com.phntechnolab.sales.repository.LoginRepository
import com.phntechnolab.sales.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSchoolViewModel @Inject constructor(private val repositories: AddSchoolRepository) : ViewModel() {

    var _oldSchoolData: MutableLiveData<SchoolData?> = MutableLiveData()
    val oldSchoolData: LiveData<SchoolData?>
        get() = _oldSchoolData

    var _newSchoolData: MutableLiveData<SchoolData?> = MutableLiveData()
    val newSchoolData: LiveData<SchoolData?>
        get() = _newSchoolData

    val addSchoolResponse: LiveData<NetworkResult<CustomResponse>>
        get() = repositories.addSchoolResponse

    fun setOldSchoolData(data: SchoolData?){
        _oldSchoolData.postValue(data)
    }

    fun setNewSchoolData(data: SchoolData?){
        _newSchoolData.postValue(data)
    }

    fun addNewSchool(schoolData: SchoolData){
        viewModelScope.launch {
            repositories.addNewSchool(schoolData)
        }
    }

}