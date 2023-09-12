package com.phntechnolab.sales

import android.app.Application
import com.phntechnolab.sales.di.ApplicationComponent
import com.phntechnolab.sales.di.DaggerApplicationComponent
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class CustomApplication: Application() {
    lateinit var applicationComponent: ApplicationComponent
    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent.factory().create(this)

        // initialize timber in application class
        // initialize timber in application class
        Timber.plant(Timber.DebugTree())
    }
}