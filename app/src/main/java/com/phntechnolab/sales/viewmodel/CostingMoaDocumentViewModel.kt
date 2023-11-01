package com.phntechnolab.sales.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
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

    private fun returntoJson(): MultipartBody {
        val body = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
            addFormDataPart("id", _moaDocumentData.value?.id ?: "")
            addFormDataPart("school_id", _moaDocumentData.value?.schoolId ?: "")
            addFormDataPart("interested_intake", _moaDocumentData.value?.interestedIntake ?: "")
            addFormDataPart("final_costing", _moaDocumentData.value?.finalCosting ?: "")
//            addFormDataPart("agreement_duration", _moaDocumentData.value?.agreementDuration ?: "")
//            addFormDataPart(
//                "disscussed_with_whom",
//                _moaDocumentData.value?.disscussedWithWhom ?: ""
//            )
            addFormDataPart("designation", _moaDocumentData.value?.designation ?: "")
            addFormDataPart("remark", _moaDocumentData.value?.remark ?: "")
            addFormDataPart("status", _moaDocumentData.value?.status ?: "")
//            if(_requestFile != null){
            addFormDataPart("moa_file", "$imageName.pdf", _requestFile!!)
//            }
        }.build()
        return body
    }

    fun isProposeCostingFieldsValid(context: Context): Boolean {
        val isPriceDiscussedPending = _proposeCostingData.value?.priceDiscussed != "yes"

        val isPricepPerStudentPending =
            _proposeCostingData.value?.pricePerStudent.isNullOrBlank()

        val isQuotationValidityPending = _proposeCostingData.value?.quotationValidity.isNullOrBlank()

        val isQuotationDurationPending = _proposeCostingData.value?.quotationDuration.isNullOrBlank()

        val isDesignationPending = _proposeCostingData.value?.designation.isNullOrBlank()

        val isAuthorityNamePending = _proposeCostingData.value?.authorityName.isNullOrBlank()

        val isConversationRatioNotSelected =
            _proposeCostingData.value?.conversationRatio.isNullOrBlank()

        val isDateAndTimeEmpty = if(_proposeCostingData.value?.meetDateTime.isNullOrBlank()){
            Toast.makeText(context, "Please enter date and time", Toast.LENGTH_SHORT).show()
            true
        }else{
            false
        }

        return if (isPricepPerStudentPending || isPriceDiscussedPending || isQuotationValidityPending || isConversationRatioNotSelected || isQuotationDurationPending || isDesignationPending || isAuthorityNamePending || isDateAndTimeEmpty) {
            Toast.makeText(
                context,
                context.getString(com.phntechnolab.sales.R.string.please_fill_all_the_mendate_and_mark_yes_details),
                Toast.LENGTH_LONG
            ).show()
            false
        } else {
            true
        }
    }

    fun isMOADocumentFieldsValid(context: Context): Boolean {

        val isTotalInterestedIntakeNotFilled =
            _moaDocumentData.value?.interestedIntake.isNullOrBlank()

        val isCostingPerStudentNotFilled =
            _moaDocumentData.value?.finalCosting.isNullOrBlank()

        val isAgreementDurationNotSelected =
            _moaDocumentData.value?.quotationDuration.isNullOrBlank()

        val isDiscussedWithWhoomNotSelected =
            _moaDocumentData.value?.designation.isNullOrBlank()

        val isAuthorityNameEmpty = _moaDocumentData.value?.authorityName.isNullOrBlank()

        val isDesignationNotSelected = _moaDocumentData.value?.designation.isNullOrBlank()

        val isMoaDocumentNotUploaded = _requestFile == null


        return if (isTotalInterestedIntakeNotFilled || isCostingPerStudentNotFilled || isDiscussedWithWhoomNotSelected || isAuthorityNameEmpty || isDesignationNotSelected || isAgreementDurationNotSelected || isMoaDocumentNotUploaded) {
            if (isMoaDocumentNotUploaded) {
                Toast.makeText(
                    context,
                    context.getString(com.phntechnolab.sales.R.string.please_upload_moa_document),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    context.getString(com.phntechnolab.sales.R.string.please_fill_all_the_mendate_details),
                    Toast.LENGTH_SHORT
                ).show()
            }
            false
        } else {
            true
        }
    }
}