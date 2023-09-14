package com.phntechnolab.sales.model

import com.google.gson.annotations.SerializedName

data class SchoolData (
    @SerializedName("id") var id : Int?    = null,
    @SerializedName("school_id") var schoolId : String? = null,
    @SerializedName("school_name") var schoolName : String? = null,
    @SerializedName("school_address") var schoolAddress : String? = null,
    @SerializedName("board") var board : String? = null,
    @SerializedName("intake") var intake : Int? = null,
    @SerializedName("total_class_room") var totalClassRoom : Int? = null,
    @SerializedName("email") var email : String? = null,
    @SerializedName("principal_name") var principalName : String? = null,
    @SerializedName("director_name") var directorName : String? = null,
    @SerializedName("avg_school_fees") var avgSchoolFees : String? = null,
    @SerializedName("existing_lab") var existingLab : String? = null,
    @SerializedName("exp_quated_value") var expQuatedValue : String? = null,
    @SerializedName("lead_type") var leadType : String? = null,
    @SerializedName("next_followup") var nextFollowup : String? = null,
    @SerializedName("followup_type") var followupType : String? = null,
    @SerializedName("upload_img") var uploadImg : String? = null,
    @SerializedName("remark") var remark : String? = null,
    @SerializedName("status") var status : String? = null,
    @SerializedName("user_id") var userId : String? = null,
    @SerializedName("created_at") var createdAt : String? = null,
    @SerializedName("updated_at") var updatedAt : String? = null
)