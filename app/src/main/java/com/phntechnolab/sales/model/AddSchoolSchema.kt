package com.phntechnolab.sales.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import okhttp3.MultipartBody

@Parcelize
class AddSchoolSchema(
    @SerializedName("school_name") var schoolName: String = "",
    @SerializedName("school_address") var schoolAddress: String = "",
    @SerializedName("board") var board: String = "",
    @SerializedName("intake") var intake: Int = 0,
    @SerializedName("total_class_room") var totalClassRoom: Int = 0,
    @SerializedName("email") var email: String = "",
    @SerializedName("co_name") var coName: String = "",
    @SerializedName("co_mobile_no") var coMobileNo: String = "",
    @SerializedName("director_name") var directorName: String = "",
    @SerializedName("director_mob_no") var directorMobNo: String = "",
    @SerializedName("avg_school_fees") var avgSchoolFees: String = "",
    @SerializedName("existing_lab") var existingLab: String = "",
    @SerializedName("exp_quated_value") var expQuatedValue: String = "",
    @SerializedName("lead_type") var leadType: String = "",
    @SerializedName("interested") var interested: String = "",
    @SerializedName("next_followup") var nextFollowup: String = "",
    @SerializedName("followup_type") var followupType: String = "",
    @SerializedName("upload_img") var uploadImg: String = "",
    @SerializedName("remark") var remark: String = ""
//    @SerializedName("school_image") val image: MultipartBody.Part
) : Parcelable