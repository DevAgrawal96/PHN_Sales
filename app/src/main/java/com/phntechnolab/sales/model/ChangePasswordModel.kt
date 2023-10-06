package com.phntechnolab.sales.model

data class ChangePasswordModel(
    val confirm_password: String,
    val new_password: String,
    val old_password: String
)