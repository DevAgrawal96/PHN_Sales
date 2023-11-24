package com.phntechnolab.sales.api

import com.phntechnolab.sales.model.AddSchoolSchema
import com.phntechnolab.sales.model.ChangePasswordModel
import com.phntechnolab.sales.model.CoordinatorData
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.DMData
import com.phntechnolab.sales.model.InstallmentData
import com.phntechnolab.sales.model.MOADocumentData
import com.phntechnolab.sales.model.ProposeCostingData
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.model.SchoolPaginationData
import com.phntechnolab.sales.model.UserDataModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApi {

    @GET("api/allschools")
    suspend fun getAllSchoolData(): Response<List<SchoolData>>

    @GET("api/paginationdata")
    suspend fun getAllSchoolDataPage(@Query("page") page: Int): Response<SchoolPaginationData>

    @POST("api/refresh")
    suspend fun refereshToken(): Response<CustomResponse>

    @POST("api/logout")
    suspend fun logout(): Response<CustomResponse>

    @POST("api/changepassword")
    suspend fun changePassword(@Body changePasswordModel: ChangePasswordModel): Response<CustomResponse>

    @GET("api/token_validate")
    suspend fun tokenCheck(): Response<CustomResponse>

    @Multipart
    @POST("api/imageupload")
    suspend fun uploadImg(
        @Part image: MultipartBody.Part,
        @Part("image_name") imageName: String
    ): Response<CustomResponse>

    @GET("api/user-profile")
    suspend fun getUserProfileData(): Response<UserDataModel>

    @POST("api/addschool")
    suspend fun addSchool(@Body schoolDetails: MultipartBody): Response<CustomResponse>

    @PUT("api/updateschool/{id}")
    suspend fun updateSchoolData(
        @Path(value = "id") id: String,
        @Body schoolDetails: AddSchoolSchema
    ): Response<CustomResponse>

    @POST("api/installment")
    suspend fun uploadInstallmentData(
        @Body schoolDetails: InstallmentData
    ): Response<CustomResponse>

    @POST("api/updateimage/{id}")
    suspend fun updateSchoolImage(
        @Path(value = "id") id: Int,
        @Body imageDetails: MultipartBody
    ): Response<CustomResponse>

    @POST("api/uploadreciept/{school_id}")
    suspend fun updateInstallmentImage(
        @Path(value = "school_id") schoolId: String,
        @Body imageDetails: MultipartBody
    ): Response<CustomResponse>

    @POST("api/coordinatormeet")
    suspend fun updateCoordinaterMeet(@Body coordinatorMeetDetails: CoordinatorData): Response<CustomResponse>

    @POST("api/directormeet")
    suspend fun updateDMMeet(@Body dmMeetDetails: DMData): Response<CustomResponse>

    @POST("api/propsecosting")
    suspend fun proposeCostingApi(@Body proposeCostingData: ProposeCostingData): Response<CustomResponse>

    @POST("api/moadocument")
    suspend fun moaDocumentData(@Body moaDocumentData: MOADocumentData): Response<CustomResponse>
    @POST("api/uploadmoa/{school_id}")
    suspend fun moaDocumentFileUpload(@Path(value = "school_id") schoolId: String,@Body moaDocumentData: MultipartBody): Response<CustomResponse>
}