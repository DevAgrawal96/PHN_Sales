package com.phntechnolab.sales.repository

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phntechnolab.sales.R
import com.phntechnolab.sales.api.RetrofitApi
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
    private val application: Application,
    private val retrofitApi: RetrofitApi
) {

    private val _allSchoolMutableLiveData = MutableLiveData<NetworkResult<List<SchoolData>>>()
    val allSchoolMutableLiveData: LiveData<NetworkResult<List<SchoolData>>>
        get() = _allSchoolMutableLiveData

    private val _todayMeetingMutableLiveData = MutableLiveData<List<SchoolData>>()
    val todayMeetingMutableLiveData: LiveData<List<SchoolData>>
        get() = _todayMeetingMutableLiveData

    private val _tomorrowMeetingMutableLiveData = MutableLiveData<List<SchoolData>>()
    val tomorrowMeetingMutableLiveData: LiveData<List<SchoolData>>
        get() = _tomorrowMeetingMutableLiveData

    private val _upcomingMeetingMutableLiveData = MutableLiveData<List<SchoolData>>()
    val upcomingMeetingMutableLiveData: LiveData<List<SchoolData>>
        get() = _upcomingMeetingMutableLiveData


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
                            application.getString(R.string.something_went_wrong),
                            ArrayList()
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
                            "",
                            ArrayList()
                        )
                    )
                }

            } catch (e: Exception) {
                NetworkResult.Error(
                    application.getString(R.string.something_went_wrong),
                    null
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

    fun todayMeetingDataSetup() {
        val date = Calendar.getInstance().time

        Timber.e("Today Date")
        Timber.e(date.toString())
        _todayMeetingMutableLiveData.postValue(_allSchoolMutableLiveData.value?.data?.filter {
            formattedDate(it.nextFollowup.replace("/", "-")) == date
        })

    }

    fun tomorrowMeetingDataSetup() {
        val date = Calendar.getInstance()
//        date.time = Date()
        date.set(Date().year, Date().month, Date().date, 0, 0, 0)
        date.add(Calendar.DATE, 1)

        Timber.e("Tomorrow Date")
        Timber.e(date.time.toString())
        Timber.e(Date().toString())
        _tomorrowMeetingMutableLiveData.postValue(_allSchoolMutableLiveData.value?.data?.filter {
            formattedDate(it.nextFollowup.replace("/", "-").split(" ")[0]) ==  date.time
        })
    }

    fun upcomingMeetingDataSetup() {
        val date = Calendar.getInstance()
        date.add(Calendar.DATE, 1)

        Timber.e("Upcoming Date")
        Timber.e(date.time.toString())

        _upcomingMeetingMutableLiveData.postValue(_allSchoolMutableLiveData.value?.data?.filter {
            formattedDate(it.nextFollowup.replace("/", "-").split(" ")[0]) >= date.time
        })
    }

    private fun formattedDate(oldDate: String): Date {
        val dateFormat_yyyyMMddHHmmss = SimpleDateFormat(
            "dd-MM-yyyy", Locale.ENGLISH
        )
        val date = dateFormat_yyyyMMddHHmmss.parse(oldDate)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return date
    }
}