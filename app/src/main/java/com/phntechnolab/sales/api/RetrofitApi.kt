package com.phntechnolab.sales.api

import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.LoginDetails
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.model.UserResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitApi {
    @POST("api/login")
    suspend fun getLoginDetails(@Body loginDetails: LoginDetails):  Response<UserResponse>

    @GET("api/allschools")
    suspend fun getAllSchoolData():  Response<List<SchoolData>>

    @POST("api/refresh")
    suspend fun refereshToken():  Response<CustomResponse>

    @GET("api/token_validate")
    suspend fun tokenCheck(): Response<CustomResponse>

    @POST("api/addschool")
    suspend fun addSchool(@Body schoolDetails: RequestBody): Response<CustomResponse>

    @PUT("api/updateschool/{id} ")
    suspend fun updateSchoolData(@Path(value = "id") id: String, @Body schoolDetails: RequestBody): Response<CustomResponse>

}