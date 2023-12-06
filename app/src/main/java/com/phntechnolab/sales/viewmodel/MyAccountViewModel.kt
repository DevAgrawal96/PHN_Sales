package com.phntechnolab.sales.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.model.UserDataModel
import com.phntechnolab.sales.util.AppEvent
import javax.inject.Inject

class MyAccountViewModel @Inject constructor() : ViewModel() {

    private var _newUserData: MutableLiveData<UserDataModel?> = MutableLiveData()
    val newUserData: LiveData<UserDataModel?>
        get() = _newUserData

    var _appEvent: MutableLiveData<AppEvent?> = MutableLiveData()
    val appEvent: LiveData<AppEvent?>
        get() = _appEvent
//    val userData = ObservableField<UserDataModel>()


    fun setNewUserData(data: UserDataModel?) {
//        userData.set(data)
        _newUserData.postValue(data)
    }


    fun openPreviusScreen() {
        _appEvent.postValue(AppEvent.BackScreen(0))
    }
}