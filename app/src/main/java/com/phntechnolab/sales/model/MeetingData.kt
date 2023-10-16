package com.phntechnolab.sales.model

data class MeetingData(
    var taskName: String? = null,
    var taskDateFilter: String? = null,
    var data: SchoolData? = null,
    var meetingWithWhoom: String? = null,
    var dateTime: String? = null
)