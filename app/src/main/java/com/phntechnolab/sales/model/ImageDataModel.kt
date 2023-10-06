package com.phntechnolab.sales.model

import okhttp3.MultipartBody

data class ImageDataModel(
    val image: MultipartBody.Part,
    val image_name: String
)