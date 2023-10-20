package com.phntechnolab.sales.api

import com.phntechnolab.sales.model.LoginDetails
import com.phntechnolab.sales.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitApi {
    @POST("api/login")
    suspend fun getLoginDetails(@Body loginDetails: LoginDetails): Response<UserResponse>

}