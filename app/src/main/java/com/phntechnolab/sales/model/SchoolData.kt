package com.phntechnolab.sales.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class SchoolData (
    @SerializedName("id") var id : Int    = 0,
    @SerializedName("school_id") var schoolId : String = "",
    @SerializedName("school_name") var schoolName : String = "",
    @SerializedName("school_address") var schoolAddress : String = "",
    @SerializedName("board") var board : String = "",
    @SerializedName("intake") var intake : Int = 0,
    @SerializedName("total_class_room") var totalClassRoom : Int = 0,
    @SerializedName("email") var email : String = "",
    @SerializedName("co_name") var coName : String = "",
    @SerializedName("co_mobile_no") var coMobileNo : String = "",
    @SerializedName("director_name") var directorName : String = "",
    @SerializedName("director_mob_no") var directorMobNo : String = "",
    @SerializedName("avg_school_fees") var avgSchoolFees : String = "",
    @SerializedName("existing_lab") var existingLab : String = "",
    @SerializedName("exp_quated_value") var expQuatedValue : String = "",
    @SerializedName("lead_type") var leadType : String = "",
    @SerializedName("next_followup") var nextFollowup : String = "",
    @SerializedName("followup_type") var followupType : String = "",
    @SerializedName("upload_img") var uploadImg : String = "",
    @SerializedName("remark") var remark : String = "",
    @SerializedName("status") var status : String = "",
    @SerializedName("user_id") var userId : String = "",
    @SerializedName("created_at") var createdAt : String = "",
    @SerializedName("updated_at") var updatedAt : String = ""
): Parcelable