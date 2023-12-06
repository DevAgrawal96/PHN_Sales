package com.phntechnolab.sales.util

sealed class AppEvent(){
    data class ToastEvent(val message : String) : AppEvent()
    data class ToastResEvent(val message : Int) : AppEvent()
    data class LoadingEvent(val state : Boolean) : AppEvent()
    data class ChangeStep(val step: Int?) : AppEvent()
    data class BackScreen(val screenID: Int?) : AppEvent()
}