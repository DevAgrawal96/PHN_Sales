package com.phntechnolab.sales.viewmodel

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.InstallmentData
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

    private var _position: Int = 0
    private var _count: Int = 0
    private val iPosition get() = _position
    private val iCount get() = _count

    var _requestFileAdvancePayment: RequestBody? = null



    var _requestFile1: RequestBody? = null
    var is_requestFile1 = false
    var imageData1: MultipartBody.Part? = null
    var imageName1: String? = null
    var imagesize1: Int? = null

    var _requestFile2: RequestBody? = null
    var is_requestFile2 = false
    var imageData2: MultipartBody.Part? = null
    var imageName2: String? = null
    var imagesize2: Int? = null

    var _requestFile3: RequestBody? = null
    var is_requestFile3 = false
    var imageData3: MultipartBody.Part? = null
    var imageName3: String? = null
    var imagesize3: Int? = null

    var _requestFile4: RequestBody? = null
    var is_requestFile4 = false
    var imageData4: MultipartBody.Part? = null
    var imageName4: String? = null
    var imagesize4: Int? = null

    private var _installmentData: MutableLiveData<SchoolData?> = MutableLiveData()
    val installmentData: LiveData<SchoolData?>
        get() = _installmentData

    val addInstallmentResponse: LiveData<NetworkResult<CustomResponse>>
        get() = repository.installmentResponse

    val addInstallmentImageResponse: LiveData<NetworkResult<CustomResponse>>
        get() = repository.installmentImageResponse

    fun getPosition(): Int {
        return iPosition
    }

    fun setPosition(position: Int) {
        _position = position
    }

    fun getCount(): Int {
        return iCount
    }

    fun setCount(count: Int) {
        _count = count
    }


    fun setInstallmentData(data: SchoolData?) {
        _installmentData.postValue(data)
    }

    fun setInstallmentsData(data: InstallmentData) {
        _installmentData.value?.installmentData = data
        _installmentData.postValue(_installmentData.value)
    }

    fun uploadInstallmentDocument(
        documentUri: Uri,
        requireContext: Context,
        installmentNumber: Int
    ) {
        when (installmentNumber) {
            0 -> {
                val fileDir = requireContext.filesDir
                val type = requireContext.contentResolver.getType(documentUri);
                val fileExtention = MimeTypeMap.getSingleton().getExtensionFromMimeType(type)
                val file = File(fileDir, "moaDocument.${fileExtention}")
                val inputStream = requireContext.contentResolver.openInputStream(documentUri)
                val fileOutputStream = FileOutputStream(file)
                inputStream?.copyTo(fileOutputStream)

                var requestFile: RequestBody? = null
                if (fileExtention == "jpg" || fileExtention == "png") {
                    requestFile = RequestBody.create(
                        MediaType.parse("image/*"),
                        file
                    )
                } else if (fileExtention == "pdf") {
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
                imageName1 = sdf.format(Date()) + "." + fileExtention
            }

            1 -> {
                val fileDir = requireContext.filesDir
                val type = requireContext.contentResolver.getType(documentUri);
                val fileExtention = MimeTypeMap.getSingleton().getExtensionFromMimeType(type)
                val file = File(fileDir, "moaDocument.${fileExtention}")
                val inputStream = requireContext.contentResolver.openInputStream(documentUri)
                val fileOutputStream = FileOutputStream(file)
                inputStream?.copyTo(fileOutputStream)

                var requestFile: RequestBody? = null
                if (fileExtention == "jpg" || fileExtention == "png") {
                    requestFile = RequestBody.create(
                        MediaType.parse("image/*"),
                        file
                    )
                } else if (fileExtention == "pdf") {
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
                imageName2 = sdf.format(Date()) + "." + fileExtention
            }

            2 -> {
                val fileDir = requireContext.filesDir
                val type = requireContext.contentResolver.getType(documentUri);
                val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(type)
                val file = File(fileDir, "moaDocument.${fileExtension}")
                val inputStream = requireContext.contentResolver.openInputStream(documentUri)
                val fileOutputStream = FileOutputStream(file)
                inputStream?.copyTo(fileOutputStream)

                var requestFile: RequestBody? = null
                if (fileExtension == "jpg" || fileExtension == "png") {
                    requestFile = RequestBody.create(
                        MediaType.parse("image/*"),
                        file
                    )
                } else if (fileExtension == "pdf") {
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
                imageName3 = sdf.format(Date()) + "." + fileExtension
            }

            3 -> {
                val fileDir = requireContext.filesDir
                val type = requireContext.contentResolver.getType(documentUri);
                val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(type)
                val file = File(fileDir, "advancePayment.${fileExtension}")
                val inputStream = requireContext.contentResolver.openInputStream(documentUri)
                val fileOutputStream = FileOutputStream(file)
                inputStream?.copyTo(fileOutputStream)

                var requestFile: RequestBody? = null
                if (fileExtension == "jpg" || fileExtension == "png") {
                    requestFile = RequestBody.create(
                        MediaType.parse("image/*"),
                        file
                    )
                } else if (fileExtension == "pdf") {
                    requestFile = RequestBody.create(
                        MediaType.parse("application/pdf"),
                        file
                    )
                }

                _requestFile4 = requestFile
                is_requestFile4 = true
                val part =
                    MultipartBody.Part.createFormData("advance_payment", file.name, requestFile!!)
                imagesize4 = Integer.parseInt((file.length() / 1024).toString())
                imageData4 = part
                val sdf = SimpleDateFormat("ddMyyyyhhmmss")
                imageName4 = sdf.format(Date()) + "." + fileExtension
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
        _requestFile4?.let {
            multipartBody.addFormDataPart(
                "advance_payment_receipt", imageName4,
                it
            )
        }
//        }
        return multipartBody.build()
    }

}