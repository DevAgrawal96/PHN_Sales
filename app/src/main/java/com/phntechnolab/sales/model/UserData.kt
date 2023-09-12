package com.phntechnolab.sales.model

data class UserResponse(
    val access_token: String?,
    val token_type: String?,
    val expires_in: String?,
    var status_code: Int?,
    val message: String?,
    val user: UserData?,
    val msg: String? = null
)
data class UserData(
    val userId: Int,
    val id: Int,
    val name: String?,
    val email: String?,
    val mobile_no: String?,
    val email_verified_at: String?,
    val allocated_area: String?,
    val password: String?,
    val role: String?
)
