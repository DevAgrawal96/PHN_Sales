package com.phntechnolab.sales.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.phntechnolab.sales.model.AddSchoolSchema
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.repository.AddSchoolRepository
import com.phntechnolab.sales.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddSchoolViewModel @Inject constructor(private val repositories: AddSchoolRepository) :
    ViewModel() {

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

    val uploadImgResponse: LiveData<NetworkResult<CustomResponse>>
        get() = repositories.imageUploadResponse

    var imageData: MultipartBody.Part? = null
    var _requestFile: RequestBody? = null
    var imageName: String? = null

    fun setOldSchoolData(data: SchoolData?) {
        _oldSchoolData.postValue(data)
    }

    fun setNewSchoolData(data: SchoolData?) {
        _newSchoolData.postValue(data)
    }

    fun addNewSchool() {

        val multiPartBody: MultipartBody = returnJsonData(_newSchoolData.value?: SchoolData(), true)

        val addSchoolData: AddSchoolSchema = returnSchoolSchema()
        viewModelScope.launch {

            repositories.addNewSchool(multiPartBody)
        }
    }

    fun uploadImage(){
        viewModelScope.launch {
            Log.e("Upload images", _newSchoolData.value?.id.toString())
            Log.e("Upload images", Gson().toJson(_requestFile))
            Log.e("Upload images", (_requestFile != null).toString())
            repositories.uploadImage(_newSchoolData.value?.id ?: 0, MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("school_image", "$imageName.jpg", _requestFile).build())
//            repositories.uploadImage(_newSchoolData.value?.id ?: 0, MultipartBody.Part.createFormData("school_image", "$imageName.jpg",  _requestFile))
        }
    }

    fun updateSchoolDetails() {

        val multiPartBody: MultipartBody = returnJsonData(_newSchoolData.value?: SchoolData(), false)

        viewModelScope.launch {
            Timber.e(Gson().toJson(newSchoolData.value))
            withContext(this.coroutineContext) {
                repositories.updateSchoolData(
                    newSchoolData.value?.id.toString() ?: "",
                    multiPartBody
                )
            }

//            withContext(this.coroutineContext){
//                uploadImage()
//            }
        }
    }

    fun uploadImage(imageUri: Uri, context: Context) {
        val fileDir = context.filesDir
        val file = File(fileDir, "image.jpg")
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val fileOutputStream = FileOutputStream(file)
        inputStream?.copyTo(fileOutputStream)

        val requestFile: RequestBody = RequestBody.create(
            MediaType.parse("image/jpg"),
            file
        )
        _requestFile = requestFile

        val part = MultipartBody.Part.createFormData("profile", file.name, requestFile)
        imageData = part
        val sdf = SimpleDateFormat("ddMyyyyhhmmss")
        imageName = sdf.format(Date())
    }

    private fun returnJsonData(data: Any, isAddSchool: Boolean): MultipartBody {
        var multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("school_name", (data as SchoolData)?.schoolName ?: "")
            .addFormDataPart("school_address", data?.schoolAddress ?: "")
            .addFormDataPart("board", data?.board ?: "")
            .addFormDataPart("intake", data?.intake.toString())
            .addFormDataPart("total_class_room", data?.totalClassRoom.toString())
            .addFormDataPart("email", data?.email ?: "")
            .addFormDataPart("co_mobile_no", data?.coMobileNo ?: "")
            .addFormDataPart("co_name", data?.coName ?: "")
            .addFormDataPart("director_name", data?.directorName ?: "")
            .addFormDataPart("director_mob_no", data?.directorMobNo ?: "")
            .addFormDataPart("avg_school_fees", data?.avgSchoolFees ?: "")
            .addFormDataPart("existing_lab", data?.existingLab ?: "")
            .addFormDataPart("exp_quated_value", data?.expQuatedValue ?: "")
            .addFormDataPart("lead_type", data?.leadType ?: "")
            .addFormDataPart("next_followup", data?.nextFollowup ?: "")
            .addFormDataPart("followup_type", data?.followupType ?: "")
            .addFormDataPart("upload_img", data?.uploadImg ?: "")
            .addFormDataPart("remark", data?.remark ?: "")

        if(isAddSchool)
            multipartBody.addFormDataPart("school_image", "$imageName.jpg", _requestFile!!)

        return multipartBody.build()
    }

    private fun returnSchoolSchema(): AddSchoolSchema {
        return AddSchoolSchema().apply {
            this.schoolName = newSchoolData.value?.schoolName ?: ""
            this.schoolAddress = newSchoolData.value?.schoolAddress ?: ""
            this.board = newSchoolData.value?.board ?: ""
            this.intake = newSchoolData.value?.intake ?: 0
            this.totalClassRoom = newSchoolData.value?.totalClassRoom ?: 0
            this.email = newSchoolData.value?.email ?: ""
            this.coMobileNo = newSchoolData.value?.coMobileNo ?: ""
            this.coName = newSchoolData.value?.coName ?: ""
            this.directorName = newSchoolData.value?.directorName ?: ""
            this.directorMobNo = newSchoolData.value?.directorMobNo ?: ""
            this.avgSchoolFees = newSchoolData.value?.avgSchoolFees ?: ""
            this.existingLab = newSchoolData.value?.existingLab ?: ""
            this.expQuatedValue = newSchoolData.value?.expQuatedValue ?: ""
            this.leadType = newSchoolData.value?.leadType ?: ""
            this.nextFollowup = newSchoolData.value?.nextFollowup ?: ""
            this.followupType = newSchoolData.value?.followupType ?: ""
        }
    }
}