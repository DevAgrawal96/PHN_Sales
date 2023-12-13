package com.phntechnolab.sales.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.repository.SearchRepository
import com.phntechnolab.sales.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repositories: SearchRepository) :
    ViewModel() {

    val schoolLiveData: MutableLiveData<NetworkResult<List<SchoolData>>>
        get() = repositories.schoolDataMutableLiveData

    val searchQueryLiveData = MutableLiveData<String>()

    val filteredSchoolsLiveData: LiveData<List<SchoolData>?> =
        searchQueryLiveData.switchMap { searchQuery ->
            if (searchQuery.isNullOrEmpty()) {
                // If the search query is blank, return the original data
                MutableLiveData(emptyList())
            } else {
                // If there's a search query, filter the original data based on the query
                MutableLiveData(schoolLiveData.value?.data.let { schoolData ->
                    schoolData?.filter {
                        it.schoolName.lowercase()
                            .contains(searchQuery.toString().lowercase())
                    }?.sortedByDescending { it.updatedAt }
                })
            }
        }

    fun getAllSchools() {
        viewModelScope.launch {
            repositories.getSchoolData()
        }
    }
}