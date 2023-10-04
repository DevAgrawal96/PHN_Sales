package com.phntechnolab.sales.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phntechnolab.sales.model.CustomResponse
import com.phntechnolab.sales.model.MOADocumentData
import com.phntechnolab.sales.model.ProposeCostingData
import com.phntechnolab.sales.repository.CostingMOADocumentRepository
import com.phntechnolab.sales.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CostingMoaDocumentViewModel @Inject constructor(private val repositories: CostingMOADocumentRepository) : ViewModel() {

    var _proposeCostingData: MutableLiveData<ProposeCostingData?> = MutableLiveData()
    val proposeCostingData: LiveData<ProposeCostingData?>
        get() = _proposeCostingData

    var _moaDocumentData: MutableLiveData<MOADocumentData?> = MutableLiveData()
    val moaDocumentData: LiveData<MOADocumentData?>
        get() = _moaDocumentData

    val proposeCostingDetails: LiveData<NetworkResult<CustomResponse>>
        get() = repositories.proposeCostingDetails

    val moaDocumentDetails: LiveData<NetworkResult<CustomResponse>>
        get() = repositories.moaDocumentDetails

    fun updateProposeCostingDetails(){

        viewModelScope.launch {
            repositories.proposeCostingData(_proposeCostingData.value?: ProposeCostingData())
        }
    }

    fun updateMoaDocumentDetails(){

        viewModelScope.launch {
            repositories.moaDocumentData(_moaDocumentData.value?: MOADocumentData())
        }
    }
}