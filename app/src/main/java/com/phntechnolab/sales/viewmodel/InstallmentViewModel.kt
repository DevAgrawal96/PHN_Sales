package com.phntechnolab.sales.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phntechnolab.sales.model.InstallmentData
import com.phntechnolab.sales.model.MOADocumentData
import com.phntechnolab.sales.model.SchoolData
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class InstallmentViewModel @Inject constructor() : ViewModel() {
    var _requestFile1: RequestBody? = null
    var imageData1: MultipartBody.Part? = null
    var imageName1: String? = null
    var imagesize1: Int? = null

    var _requestFile2: RequestBody? = null
    var imageData2: MultipartBody.Part? = null
    var imageName2: String? = null
    var imagesize2: Int? = null

    var _requestFile3: RequestBody? = null
    var imageData3: MultipartBody.Part? = null
    var imageName3: String? = null
    var imagesize3: Int? = null

    private var _oldSchoolData: MutableLiveData<InstallmentData?> = MutableLiveData()
    val oldSchoolData: LiveData<InstallmentData?>
        get() = _oldSchoolData

    fun setOldSchoolData(data: InstallmentData?) {
        _oldSchoolData.postValue(data)
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

                val part = MultipartBody.Part.createFormData("moa_document", file.name, requestFile)
                imagesize1 = Integer.parseInt((file.length() / 1024).toString())
                imageData1 = part
                val sdf = SimpleDateFormat("ddMyyyyhhmmss")
                imageName1 = sdf.format(Date())
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

                val part = MultipartBody.Part.createFormData("moa_document", file.name, requestFile)
                imagesize2 = Integer.parseInt((file.length() / 1024).toString())
                imageData2 = part
                val sdf = SimpleDateFormat("ddMyyyyhhmmss")
                imageName2 = sdf.format(Date())
            }

            2 -> {
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

                _requestFile3 = requestFile

                val part = MultipartBody.Part.createFormData("moa_document", file.name, requestFile)
                imagesize3 = Integer.parseInt((file.length() / 1024).toString())
                imageData3 = part
                val sdf = SimpleDateFormat("ddMyyyyhhmmss")
                imageName3 = sdf.format(Date())
            }
        }

    }

}