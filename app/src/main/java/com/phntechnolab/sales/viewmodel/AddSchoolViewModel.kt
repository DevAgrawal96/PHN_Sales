package com.phntechnolab.sales.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.phntechnolab.sales.model.AddSchoolSchema
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.repository.AddSchoolRepository
import com.phntechnolab.sales.repository.LoginRepository
import com.phntechnolab.sales.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber
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

    val updateSchoolResponse: LiveData<NetworkResult<CustomResponse>>
        get() = repositories.updateSchoolResponse

    fun setOldSchoolData(data: SchoolData?){
        _oldSchoolData.postValue(data)
    }

    fun setNewSchoolData(data: SchoolData?){
        _newSchoolData.postValue(data)
    }

    fun addNewSchool(){

        val multiPartBody: MultipartBody = returnJsonData()

        val addSchoolData: AddSchoolSchema = returnSchoolSchema()
        viewModelScope.launch {
            repositories.addNewSchool(addSchoolData)
        }
    }

    fun updateSchoolDetails(){

        val multiPartBody: MultipartBody = returnJsonData()

        viewModelScope.launch {
            Timber.e(Gson().toJson(newSchoolData.value))
            repositories.updateSchoolData(newSchoolData.value?.id.toString()?:"", newSchoolData.value?: SchoolData())
        }
    }

    private fun returnJsonData() : MultipartBody{
        return MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("school_name", newSchoolData.value?.schoolName?:"")
            .addFormDataPart("school_address", newSchoolData.value?.schoolAddress?:"")
            .addFormDataPart("board", newSchoolData.value?.board?:"")
            .addFormDataPart("intake", newSchoolData.value?.intake.toString())
            .addFormDataPart("total_class_room", newSchoolData.value?.totalClassRoom.toString())
            .addFormDataPart("email", newSchoolData.value?.email?:"")
            .addFormDataPart("co_mobile_no", newSchoolData.value?.coMobileNo?:"")
            .addFormDataPart("co_name", newSchoolData.value?.coName?:"")
            .addFormDataPart("director_name", newSchoolData.value?.directorName?:"")
            .addFormDataPart("director_mob_no", newSchoolData.value?.directorMobNo?:"")
            .addFormDataPart("avg_school_fees", newSchoolData.value?.avgSchoolFees?:"")
            .addFormDataPart("existing_lab", newSchoolData.value?.existingLab?:"")
            .addFormDataPart("exp_quated_value", newSchoolData.value?.expQuatedValue?:"")
            .addFormDataPart("lead_type", newSchoolData.value?.leadType?:"")
            .addFormDataPart("next_followup", newSchoolData.value?.nextFollowup?:"")
            .addFormDataPart("followup_type", newSchoolData.value?.followupType?:"")
            .addFormDataPart("upload_img", newSchoolData.value?.uploadImg?:"")
            .addFormDataPart("remark", newSchoolData.value?.remark?:"").build()
    }

    private fun returnSchoolSchema(): AddSchoolSchema{
        return AddSchoolSchema().apply {
            this.schoolName =  newSchoolData.value?.schoolName?: ""
            this.schoolAddress = newSchoolData.value?.schoolAddress?:""
            this.board = newSchoolData.value?.board?:""
            this.intake = newSchoolData.value?.intake?:0
            this.totalClassRoom = newSchoolData.value?.totalClassRoom?:0
            this.email = newSchoolData.value?.email?:""
            this.coMobileNo = newSchoolData.value?.coMobileNo?:""
            this.coName = newSchoolData.value?.coName?:""
            this.directorName = newSchoolData.value?.directorName?:""
            this.directorMobNo = newSchoolData.value?.directorMobNo?:""
            this.avgSchoolFees = newSchoolData.value?.avgSchoolFees?:""
            this.existingLab = newSchoolData.value?.existingLab?:""
            this.expQuatedValue = newSchoolData.value?.expQuatedValue?:""
            this.leadType = newSchoolData.value?.leadType?:""
            this.nextFollowup = newSchoolData.value?.nextFollowup?:""
            this.followupType = newSchoolData.value?.followupType?:""
        }
    }
}