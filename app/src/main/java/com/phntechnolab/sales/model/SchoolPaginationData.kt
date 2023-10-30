package com.phntechnolab.sales.model

import com.google.gson.annotations.SerializedName

class SchoolPaginationData {
    var school: SchoolPaginationDataSchema? = null
}

data class SchoolPaginationDataSchema(
    @SerializedName("current_page") var currentPage : String? = "",
    @SerializedName("data") var data : List<SchoolData>? = ArrayList(),
    @SerializedName("next_page_url") var nextPageUrl : String? = null
)