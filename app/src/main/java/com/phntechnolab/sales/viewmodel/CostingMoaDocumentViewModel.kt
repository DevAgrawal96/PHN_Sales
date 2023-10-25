package com.phntechnolab.sales.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.MOADocumentData
import com.phntechnolab.sales.model.ProposeCostingData
import com.phntechnolab.sales.repository.CostingMOADocumentRepository
import com.phntechnolab.sales.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CostingMoaDocumentViewModel @Inject constructor(private val repositories: CostingMOADocumentRepository) :
    ViewModel() {

    var _proposeCostingData: MutableLiveData<ProposeCostingData?> = MutableLiveData()
    val proposeCostingData: LiveData<ProposeCostingData?>
        get() = _proposeCostingData

    var _moaDocumentData: MutableLiveData<MOADocumentData?> = MutableLiveData()
    val moaDocumentData: LiveData<MOADocumentData?>
        get() = _moaDocumentData

    val proposeCostingDetails: LiveData<NetworkResult<CustomResponse>>
        get() = repositories.proposeCostingDetails

    val moaDocumentDetails: LiveData<NetworkResult<CustomResponse>>
        get() = repositories.moaDocumentDetails

    var imageData: MultipartBody.Part? = null
    var imageName: String? = null
    var _requestFile: RequestBody? = null

    fun updateProposeCostingDetails() {

        viewModelScope.launch {
            repositories.proposeCostingData(_proposeCostingData.value ?: ProposeCostingData())
        }
    }

    fun updateMoaDocumentDetails() {

        viewModelScope.launch {
            repositories.moaDocumentData(returntoJson())
        }
    }

    fun uploadDocument(documentUri: Uri, requireContext: Context) {
        val fileDir = requireContext.filesDir
        val file = File(fileDir, "moaDocument.pdf")
        val inputStream = requireContext.contentResolver.openInputStream(documentUri)
        val fileOutputStream = FileOutputStream(file)
        inputStream?.copyTo(fileOutputStream)

        val requestFile: RequestBody = RequestBody.create(
            MediaType.parse("application/pdf"),
            file
        )
        _requestFile = requestFile

        val part = MultipartBody.Part.createFormData("moa_document", file.name, requestFile)
        imageData = part
        val sdf = SimpleDateFormat("ddMyyyyhhmmss")
        imageName = sdf.format(Date())
    }

    fun returntoJson(): MultipartBody {
        val body = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
            addFormDataPart("id", _moaDocumentData.value?.id ?: "")
            addFormDataPart("school_id", _moaDocumentData.value?.schoolId ?: "")
            addFormDataPart("interested_intake", _moaDocumentData.value?.interestedIntake ?: "")
            addFormDataPart("final_costing", _moaDocumentData.value?.finalCosting ?: "")
            addFormDataPart("agreement_duration", _moaDocumentData.value?.agreementDuration ?: "")
            addFormDataPart(
                "disscussed_with_whom",
                _moaDocumentData.value?.disscussedWithWhom ?: ""
            )
            addFormDataPart("designation", _moaDocumentData.value?.designation ?: "")
            addFormDataPart("remark", _moaDocumentData.value?.remark ?: "")
            addFormDataPart("status", _moaDocumentData.value?.status ?: "")
//            if(_requestFile != null){
            addFormDataPart("moa_file", "$imageName.pdf", _requestFile!!)
//            }
        }.build()
        return body
    }
}