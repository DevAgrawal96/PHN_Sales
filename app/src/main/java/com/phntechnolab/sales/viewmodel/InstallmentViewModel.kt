package com.phntechnolab.sales.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.InstallmentData
import com.phntechnolab.sales.model.MOADocumentData
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.repository.InstallmentRepository
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
class InstallmentViewModel @Inject constructor(private var repository: InstallmentRepository) :
    ViewModel() {
    var _requestFile1: RequestBody? = null
     var is_requestFile1  = false
    var imageData1: MultipartBody.Part? = null
    var imageName1: String? = null
    var imagesize1: Int? = null

    var _requestFile2: RequestBody? = null
     var is_requestFile2  = false
    var imageData2: MultipartBody.Part? = null
    var imageName2: String? = null
    var imagesize2: Int? = null

    var _requestFile3: RequestBody? = null
     var is_requestFile3  = false
    var imageData3: MultipartBody.Part? = null
    var imageName3: String? = null
    var imagesize3: Int? = null

    private var _installmentData: MutableLiveData<SchoolData?> = MutableLiveData()
    val installmentData: LiveData<SchoolData?>
        get() = _installmentData

    val addInstallmentResponse: LiveData<NetworkResult<CustomResponse>>
        get() = repository.installmentResponse

    val addInstallmentImageResponse: LiveData<NetworkResult<CustomResponse>>
        get() = repository.installmentImageResponse

    fun setInstallmentData(data: SchoolData?) {
        _installmentData.postValue(data)
    }

    fun setInstallmentsData(data: InstallmentData){
        _installmentData.value?.installmentData = data
        _installmentData.postValue(_installmentData.value)
    }
    fun uploadInstallmentDocument(
        documentUri: Uri,
        requireContext: Context,
        fileType: String,
        installmentNumber: Int
    ) {
        when (installmentNumber) {
            0 -> {
                val fileDir = requireContext.filesDir
                val file = File(fileDir, "moaDocument.$fileType")
                Timber.e("$fileDir")
                val inputStream = requireContext.contentResolver.openInputStream(documentUri)
                val fileOutputStream = FileOutputStream(file)
                inputStream?.copyTo(fileOutputStream)

                var requestFile: RequestBody? = null
                if (fileType == "jpg" || fileType == "png") {
                    requestFile = RequestBody.create(
                        MediaType.parse("image/*"),
                        file
                    )
                } else if (fileType == "pdf") {
                    requestFile = RequestBody.create(
                        MediaType.parse("application/pdf"),
                        file
                    )
                }
                _requestFile1 = requestFile
                is_requestFile1 = true
                val part = MultipartBody.Part.createFormData("moa_document", file.name, requestFile)
                imagesize1 = Integer.parseInt((file.length() / 1024).toString())
                imageData1 = part
                val sdf = SimpleDateFormat("ddMyyyyhhmmss")
                imageName1 = sdf.format(Date()) + "." + fileType
            }

            1 -> {
                val fileDir = requireContext.filesDir
                val file = File(fileDir, "moaDocument.$fileType")
                val inputStream = requireContext.contentResolver.openInputStream(documentUri)
                val fileOutputStream = FileOutputStream(file)
                inputStream?.copyTo(fileOutputStream)

                var requestFile: RequestBody? = null
                if (fileType == "jpg" || fileType == "png") {
                    requestFile = RequestBody.create(
                        MediaType.parse("image/*"),
                        file
                    )
                } else if (fileType == "pdf") {
                    requestFile = RequestBody.create(
                        MediaType.parse("application/pdf"),
                        file
                    )
                }
                _requestFile2 = requestFile
                is_requestFile2 = true
                val part = MultipartBody.Part.createFormData("moa_document", file.name, requestFile)
                imagesize2 = Integer.parseInt((file.length() / 1024).toString())
                imageData2 = part
                val sdf = SimpleDateFormat("ddMyyyyhhmmss")
                imageName2 = sdf.format(Date()) + "." + fileType
            }

            2 -> {
                val fileDir = requireContext.filesDir
                Timber.e("$fileDir")
                val file = File(fileDir, "moaDocument.$fileType")
                val inputStream = requireContext.contentResolver.openInputStream(documentUri)
                val fileOutputStream = FileOutputStream(file)
                inputStream?.copyTo(fileOutputStream)

                var requestFile: RequestBody? = null
                if (fileType == "jpg" || fileType == "png") {
                    requestFile = RequestBody.create(
                        MediaType.parse("image/*"),
                        file
                    )
                } else if (fileType == "pdf") {
                    requestFile = RequestBody.create(
                        MediaType.parse("application/pdf"),
                        file
                    )
                }

                _requestFile3 = requestFile
                is_requestFile3 = true
                val part = MultipartBody.Part.createFormData("moa_document", file.name, requestFile)
                imagesize3 = Integer.parseInt((file.length() / 1024).toString())
                imageData3 = part
                val sdf = SimpleDateFormat("ddMyyyyhhmmss")
                imageName3 = sdf.format(Date()) + "." + fileType
            }
        }

    }

    fun addNewInstallment(data: InstallmentData) {
        viewModelScope.launch {
            repository.uploadInstallmentData(
                data
            )
        }
    }

    fun uploadInstallmentImages() {
        val multiPartBody: MultipartBody =
            returnJsonData()
        viewModelScope.launch {
            repository.uploadInstallmentImage(
                _installmentData.value?.schoolId!! ?: "",
                multiPartBody
            )
        }
    }

    private fun returnJsonData(): MultipartBody {
        val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
//        if (is_requestFile1) {
            Timber.e("is_requestFile1")
            _requestFile1?.let {
                multipartBody.addFormDataPart(
                    "first_installment_reciept", imageName1,
                    it
                )
            }
//        }
//        if (is_requestFile2) {
            Timber.e("is_requestFile2")
            _requestFile2?.let {
                multipartBody.addFormDataPart(
                    "second_installment_reciept", imageName2,
                    it
                )
            }
//        }
//        if (is_requestFile3) {
            Timber.e("is_requestFile3")
            _requestFile3?.let {
                multipartBody.addFormDataPart(
                    "third_installment_reciept", imageName3,
                    it
                )
            }
//        }
        return multipartBody.build()
    }

}