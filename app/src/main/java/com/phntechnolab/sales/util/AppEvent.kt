package com.phntechnolab.sales.util

sealed class AppEvent(){
    data class ToastEvent(val message : String) : AppEvent()
    data class LoadingEvent(val state : Boolean) : AppEvent()
}