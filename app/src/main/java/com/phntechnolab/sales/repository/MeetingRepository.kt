package com.phntechnolab.sales.repository

import android.app.Application
import android.widget.Switch
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.RetrofitApi
import com.phntechnolab.sales.model.MeetingData
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.NetworkUtils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class MeetingRepository @Inject constructor(
    private val application: Application, private val retrofitApi: RetrofitApi
) {

    private val _allSchoolMutableLiveData = MutableLiveData<NetworkResult<List<SchoolData>>>()
    val allSchoolMutableLiveData: LiveData<NetworkResult<List<SchoolData>>>
        get() = _allSchoolMutableLiveData

    private val _meetingsData = MutableLiveData<List<MeetingData>>()
    val meetingsData: LiveData<List<MeetingData>>
        get() = _meetingsData

    suspend fun getSchoolData() {
        if (NetworkUtils.isInternetAvailable(application)) {
            try {
                val result = retrofitApi.getAllSchoolData()
                if (result.isSuccessful && result?.body() != null) {

                    _allSchoolMutableLiveData.postValue(NetworkResult.Success(result.body()))
                } else if (result.errorBody() != null) {
                    Toast.makeText(
                        application,
                        application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                        Toast.LENGTH_LONG
                    ).show()
                    _allSchoolMutableLiveData.postValue(
                        NetworkResult.Error(
                            application.getString(R.string.something_went_wrong), ArrayList()
                        )
                    )
                } else {
                    Toast.makeText(
                        application,
                        application.resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                        Toast.LENGTH_LONG
                    ).show()
                    _allSchoolMutableLiveData.postValue(


                        NetworkResult.Error(
                            "", ArrayList()
                        )
                    )
                }

            } catch (e: Exception) {
                NetworkResult.Error(
                    application.getString(R.string.something_went_wrong), null
                )
                Toast.makeText(
                    application,
                    application.resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                application,
                application.resources.getString(R.string.please_connection_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun formattedDate(oldDate: String): Date {
        val dateFormat_yyyyMMddHHmmss = SimpleDateFormat(
            "dd-MM-yyyy", Locale.ENGLISH
        )
        val date = dateFormat_yyyyMMddHHmmss.parse(oldDate)
        val calendar = Calendar.getInstance()
        calendar.time = date
//        calendar.set(date.year, date.month, date.date, 0 ,0 ,0)
        Timber.e(calendar.time.toString())
        return calendar.time
    }

    fun segregateData() {
        val data = ArrayList<MeetingData>()
        _allSchoolMutableLiveData.value?.data?.forEach {
            if (!it.proposeCostingData?.nextMeet.isNullOrEmpty() && todayTomorrowUpcomingMeetingDateCheck(it.proposeCostingData?.nextMeet?:"") != "wrong" ) {
                val date =
                    todayTomorrowUpcomingMeetingDateCheck(it.proposeCostingData.nextMeet?.replace("/", "-")?.split(" ")?.get(0) ?: "")
                data.add(MeetingData("proposecosting", date, it, "MOA Document"))

            } else if ((!it.director?.nextMeetDateDm.isNullOrEmpty() && todayTomorrowUpcomingMeetingDateCheck(it.director?.nextMeetDateDm?:"") != "wrong") || !it.director?.nextMeetDate.isNullOrEmpty() && todayTomorrowUpcomingMeetingDateCheck(it.director?.nextMeetDate?:"") != "wrong") {
                if(it.director?.nextMeetDate != null &&  todayTomorrowUpcomingMeetingDateCheck(it.director.nextMeetDate?.replace("/", "-")?.split(" ")?.get(0)?:"") != "wrong"){
                    val date =
                        todayTomorrowUpcomingMeetingDateCheck(it.director.nextMeetDate?.replace("/", "-")?.split(" ")?.get(0) ?: "")
                    data.add(MeetingData("coordinator", date, it, "Propose Costing"))
                }else{
                    val date =
                        todayTomorrowUpcomingMeetingDateCheck(it.director.nextMeetDateDm?.replace("/", "-")?.split(" ")?.get(0) ?: "")
                    data.add(MeetingData("coordinator", date, it, "Director"))
                }
            } else if ((!it.coordinator?.nextMeetDateDm.isNullOrEmpty() && todayTomorrowUpcomingMeetingDateCheck(it.coordinator?.nextMeetDateDm?:"") != "wrong") || (!it.coordinator?.meetDateCoordinator.isNullOrEmpty() && todayTomorrowUpcomingMeetingDateCheck(it.coordinator?.meetDateCoordinator?:"") != "wrong")) {
                if(it.coordinator?.nextMeetDateDm != null && todayTomorrowUpcomingMeetingDateCheck(it.coordinator.nextMeetDateDm?.replace("/", "-")?.split(" ")?.get(0)?:"") != "wrong"){
                    val date =
                        todayTomorrowUpcomingMeetingDateCheck(it.coordinator.nextFollowupDm?.replace("/", "-")?.split(" ")?.get(0) ?: "")
                    data.add(MeetingData("coordinator", date, it, "Director"))
                }else{
                    val date =
                        todayTomorrowUpcomingMeetingDateCheck(it.coordinator.nextMeetDateDm?.replace("/", "-")?.split(" ")?.get(0) ?: "")
                    data.add(MeetingData("coordinator", date, it, "Coordinator"))
                }
            } else {
                if(!it.nextFollowup.isNullOrEmpty() && todayTomorrowUpcomingMeetingDateCheck(it.nextFollowup ?:"") != "wrong"){
                    val date =
                        todayTomorrowUpcomingMeetingDateCheck(it.nextFollowup?.replace("/", "-")?.split(" ")?.get(0) ?: "")
                    data.add(MeetingData("basicDetails", date, it, "Coordinator"))
                }
            }
        }
        _meetingsData.postValue(data)
//        Timber.e("check dates")
//        Timber.e(todayTomorrowUpcomingMeetingDateCheck("14-10-2023 22:11"))
//        Timber.e(todayTomorrowUpcomingMeetingDateCheck("15-10-2023 22:11"))
//        Timber.e(todayTomorrowUpcomingMeetingDateCheck("16-10-2023 22:11"))
//        Timber.e(todayTomorrowUpcomingMeetingDateCheck("17-10-2023 22:11"))
    }

    private fun todayTomorrowUpcomingMeetingDateCheck(oldDate: String): String {
        if(oldDate.isNullOrEmpty())
            return "wrong"

        val formatOldDate = oldDate.replace("/", "-").split(" ")[0]
        val date = Calendar.getInstance()
        date.set(Date().year, Date().month, Date().date, 0, 0, 0)
        val formattedOldDate = formattedDate(formatOldDate)

        val tomorrowDate = Calendar.getInstance()
        tomorrowDate.set(Date().year, Date().month, Date().date, 0, 0, 0)
        tomorrowDate.add(Calendar.DATE, 1)

        return if (Date().year == formattedOldDate.year && Date().month == formattedOldDate.month && Date().date == formattedOldDate.date) {
            //check it is todays date
            "today"

        } else if (Date().year == formattedOldDate.year && Date().month == formattedOldDate.month && (Date().date + 1) == formattedOldDate.date) {
            //check it is tomorrow date
            "tomorrow"
        } else if (formattedOldDate.after(tomorrowDate.time)) {
            //check it is upcoming date
            "upcoming"
        } else {
            "wrong"
        }
    }
}