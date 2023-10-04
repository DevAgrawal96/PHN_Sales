package com.phntechnolab.sales.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.model.UserDataModel
import javax.inject.Inject

class MyAccountViewModel @Inject constructor() : ViewModel() {

    private var _newUserData: MutableLiveData<UserDataModel?> = MutableLiveData()
    val newUserData: LiveData<UserDataModel?>
        get() = _newUserData
//    val userData = ObservableField<UserDataModel>()


    fun setNewUserData(data: UserDataModel?) {
//        userData.set(data)
        _newUserData.postValue(data)
    }
}