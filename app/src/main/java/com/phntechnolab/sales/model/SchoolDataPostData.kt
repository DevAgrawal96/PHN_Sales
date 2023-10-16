package com.phntechnolab.sales.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class SchoolDataPost : Parcelable {
    @SerializedName("id") var id : Int = 0
        get() = field
        set(value){
            field = value ?: 0
        }

    @SerializedName("school_id") var schoolId : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("school_name") var schoolName : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("school_address") var schoolAddress : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("board") var board : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("intake") var intake : Int = 0
        set(value){
            field = value ?: 0
        }
    @SerializedName("total_class_room") var totalClassRoom : Int = 0
        set(value){
            field = value ?: 0
        }

    @SerializedName("email") var email : String = ""
        set(value){
            field = value ?: ""
        }
    @SerializedName("co_name") var coName : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("co_mobile_no") var coMobileNo : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("director_name") var directorName : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("director_mob_no") var directorMobNo : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("avg_school_fees") var avgSchoolFees : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("existing_lab") var existingLab : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("exp_quated_value") var expQuatedValue : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("lead_type") var leadType : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("interested") var interested : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("next_followup") var nextFollowup : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("followup_type") var followupType : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("upload_img") var uploadImg : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("remark") var remark : String = ""
        set(value){
            field = value ?: ""
        }
}