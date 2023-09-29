package com.phntechnolab.sales.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class SchoolData : Parcelable{
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

    @SerializedName("status") var status : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("user_id") var userId : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("created_at") var createdAt : String = ""
        set(value){
            field = value ?: ""
        }

    @SerializedName("coordinator") var coordinator : CoordinatorData = CoordinatorData()
        set(value){
            field = value
        }

    @SerializedName("director") var director : DMData = DMData()
        set(value){
            field = value
        }
}

@Parcelize
data class CoordinatorData(
    @SerializedName("id") var id : String? = null,
    @SerializedName("school_id") var schoolId : String? = "",
    @SerializedName("co_attend_meet") var coAttendMeet : String? = "",
    @SerializedName("product_demo_happen") var productDemoHappen : String? = "",
    @SerializedName("next_followup_dm") var nextFollowupDm : String? = "",
    @SerializedName("interested") var interested : String? = "",
    @SerializedName("next_meet_date_dm") var nextMeetDateDm : String? = "",
    @SerializedName("reschedule_coordinator") var rescheduleWithCoordinator : String? = "",
    @SerializedName("meet_date_coordinator") var meetDateCoordinator : String? = ""
): Parcelable


@Parcelize
data class DMData(
    @SerializedName("id") var id : String? = null,
    @SerializedName("school_id") var schoolId : String? = "",
    @SerializedName("co_attend_meet") var coAttendMeet : String? = "",
    @SerializedName("product_demo_happen") var productDemoHappen : String? = "",
    @SerializedName("next_followup_dm") var nextFollowupDm : String? = "",
    @SerializedName("interested") var interested : String? = "",
    @SerializedName("next_meet_date_dm") var nextMeetDateDm : String? = "",
):Parcelable

@Parcelize
data class ProposeCostingData(
    @SerializedName("id") var id : String? = null,
    @SerializedName("school_id") var schoolId : String? = "",
    @SerializedName("price_discussed") var priceDiscussed : String? = "",
    @SerializedName("price_per_student") var pricePerStudent : String? = "",
    @SerializedName("quotation_shared") var quotationShared : String? = "",
    @SerializedName("quotation_validity") var quotationValidity : String? = "",
    @SerializedName("payment_shedule") var paymentShedule : String? = "",
    @SerializedName("agreement_duration") var agreementDuration : String? = "",
    @SerializedName("meeting_with_whoom") var meetingWithWhoom : String? = "",
    @SerializedName("conversation_ratio") var conversationRatio : String? = "",
    @SerializedName("next_meet") var nextMeet : String? = "",
    @SerializedName("remark") var remark : String? = ""
): Parcelable

@Parcelize
data class MOADocumentData(
    @SerializedName("id") var id : String? = null,
    @SerializedName("school_id") var schoolId : String? = "",
    @SerializedName("interested_intake") var interestedIntake : String? = "",
    @SerializedName("final_costing") var finalCosting : String? = "",
    @SerializedName("agreement_duration") var agreementDuration : String? = "",
    @SerializedName("disscussed_with_whom") var disscussedWithWhom : String? = "",
    @SerializedName("designation") var designation : String? = "",
    @SerializedName("moa_file") var moaFile : String? = "",
    @SerializedName("remark") var remark : String? = "",
): Parcelable

//data class SchoolData (
//    @SerializedName("id") var id : Int = 0,
//    @SerializedName("school_id") var schoolId : String = "",
//    @SerializedName("school_name") var schoolName : String = "",
//    @SerializedName("school_address") var schoolAddress : String = "",
//    @SerializedName("board") var board : String = "",
//    @SerializedName("intake") var intake : Int = 0,
//    @SerializedName("total_class_room") var totalClassRoom : Int = 0,
//    @SerializedName("email") var email : String = "",
//    @SerializedName("co_name") var coName : String = "",
//    @SerializedName("co_mobile_no") var coMobileNo : String = "",
//    @SerializedName("director_name") var directorName : String = "",
//    @SerializedName("director_mob_no") var directorMobNo : String = "",
//    @SerializedName("avg_school_fees") var avgSchoolFees : String = "",
//    @SerializedName("existing_lab") var existingLab : String = "",
//    @SerializedName("exp_quated_value") var expQuatedValue : String = "",
//    @SerializedName("lead_type") var leadType : String = "",
//    @SerializedName("next_followup") var nextFollowup : String = "",
//    @SerializedName("followup_type") var followupType : String = "",
//    @SerializedName("upload_img") var uploadImg : String = "",
//    @SerializedName("remark") var remark : String = "",
//    @SerializedName("status") var status : String = "",
//    @SerializedName("user_id") var userId : String = "",
//    @SerializedName("created_at") var createdAt : String = "",
//    @SerializedName("updated_at") var updatedAt : String = ""
//): Parcelable