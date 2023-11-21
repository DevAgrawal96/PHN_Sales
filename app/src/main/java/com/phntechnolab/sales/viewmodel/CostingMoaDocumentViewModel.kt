package com.phntechnolab.sales.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phntechnolab.sales.Event
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
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CostingMoaDocumentViewModel @Inject constructor(private val repositories: CostingMOADocumentRepository) :
    ViewModel() {

    var _oldProposeCostingData: MutableLiveData<ProposeCostingData?> = MutableLiveData()
    val oldProposeCostingData: LiveData<ProposeCostingData?>
        get() = _oldProposeCostingData

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

    var _messageLiveData = MutableLiveData<Event<String>>()
    val messageLiveData: LiveData<Event<String>>
        get() = _messageLiveData

    private var _progressBarLiveData = MutableLiveData<Boolean>()
    val progressBarLiveData: LiveData<Boolean>
        get() = _progressBarLiveData

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
            addFormDataPart("quotation_duration", _moaDocumentData.value?.quotationDuration ?: "")
            addFormDataPart(
                "authority_name",
                _moaDocumentData.value?.authorityName ?: ""
            )
            addFormDataPart("designation", _moaDocumentData.value?.designation ?: "")
            addFormDataPart("remark", _moaDocumentData.value?.remark ?: "")
            addFormDataPart("status", _moaDocumentData.value?.status ?: "")
//            if(_moaDocumentData.value?.moaFile == null){
            addFormDataPart("moa_file", "$imageName.pdf", _requestFile!!)
//            }
        }.build()
        return body
    }

    fun isProposeCostingFieldsValid() {
        val isPriceDiscussedPending = _proposeCostingData.value?.priceDiscussed != "yes"

        val isPricepPerStudentPending =
            _proposeCostingData.value?.pricePerStudent.isNullOrBlank()

        val isQuotationValidityPending =
            _proposeCostingData.value?.quotationValidity.isNullOrBlank()

        val isQuotationDurationPending =
            _proposeCostingData.value?.quotationDuration.isNullOrBlank()

        val isDesignationPending = _proposeCostingData.value?.designation.isNullOrBlank()

        val isAuthorityNamePending = _proposeCostingData.value?.authorityName.isNullOrBlank()

        val isConversationRatioNotSelected =
            _proposeCostingData.value?.conversationRatio.isNullOrBlank()

        val isRescheduleMeeting = _proposeCostingData.value?.rescheduleMeeting == "Yes"

        val isDateAndTimeEmpty = _proposeCostingData.value?.meetDateTime.isNullOrBlank() && isRescheduleMeeting

        val checkAllDataAreSame = isAllDataAreSame()

        Timber.e("DATA ARE SAME OR NOT $checkAllDataAreSame")
        if (isRescheduleMeeting) {
            return if (isPricepPerStudentPending || isPriceDiscussedPending || isQuotationValidityPending || isConversationRatioNotSelected || isQuotationDurationPending || isDesignationPending || isAuthorityNamePending || isDateAndTimeEmpty) {
                _messageLiveData.postValue(Event("Please fill all the mandate fields to proceed."))
            } else {
                //Call Api to submit the data
                _progressBarLiveData.postValue(true)
                updateProposeCostingDetails()
            }
        } else {
            return if (isPricepPerStudentPending || isPriceDiscussedPending || isQuotationValidityPending || isConversationRatioNotSelected || isQuotationDurationPending || isDesignationPending || isAuthorityNamePending) {
                _messageLiveData.postValue(Event("Please fill all the mandate fields to proceed."))
            } else {
                //Call Api to submit the data
                _progressBarLiveData.postValue(true)
                updateProposeCostingDetails()
            }
        }
    }

    private fun isAllDataAreSame(): Boolean {
        val oldData = _oldProposeCostingData.value

        val newData = _proposeCostingData.value

        return oldData?.priceDiscussed === newData?.priceDiscussed && oldData?.pricePerStudent === newData?.pricePerStudent && oldData?.intake === newData?.intake && oldData?.emailId === newData?.emailId && oldData?.quotationValidity === newData?.quotationValidity && oldData?.quotationDuration === newData?.quotationDuration && oldData?.designation === newData?.designation && oldData?.authorityName === newData?.authorityName && oldData?.conversationRatio === newData?.conversationRatio && oldData?.rescheduleMeeting === newData?.rescheduleMeeting && oldData?.meetDateTime === newData?.meetDateTime && oldData?.remark === newData?.remark
    }

    fun isMOADocumentFieldsValid() {

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
//            if(_moaDocumentData.value?.moaFile != null) false else


        if (isTotalInterestedIntakeNotFilled || isCostingPerStudentNotFilled || isDiscussedWithWhoomNotSelected || isAuthorityNameEmpty || isDesignationNotSelected || isAgreementDurationNotSelected || isMoaDocumentNotUploaded) {
            if (isMoaDocumentNotUploaded) {
                _messageLiveData.postValue(Event("Please upload moa document."))
            } else {
                _messageLiveData.postValue(Event("Please fill all the mandate fields to proceed."))
            }
        } else {
            _progressBarLiveData.postValue(true)
            updateMoaDocumentDetails()
        }
    }

    fun changeProgressBarVisibility(mode: Boolean) {
        _progressBarLiveData.postValue(mode)
    }
}