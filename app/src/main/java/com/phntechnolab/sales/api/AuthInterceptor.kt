package com.phntechnolab.sales.api

import android.content.Context
import android.util.Log
import com.phntechnolab.sales.Modules.DataStoreProvider
import com.phntechnolab.sales.util.DataStoreManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class AuthInterceptor @Inject constructor(@ApplicationContext val appContext: Context) : Interceptor {

    @Inject
    lateinit var dataStoreProvider: DataStoreProvider

    var context: Context? = null

    public fun saveContext(context: Context) {
        this.context = context
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        // we can write log url here
        val request = chain.request().newBuilder()

        var token = runBlocking {DataStoreManager.getToken(appContext, dataStoreProvider, "authToken").toString() }

        if(token != "null") {
            Log.d("TOken", token.toString())
            request.addHeader("Authorization", "Bearer $token")
        }

        request.addHeader("Accept", "application/json")

        val boundary = "-------------" + System.currentTimeMillis()

        request.addHeader("Content-type", "multipart/form-data; boundary=$boundary")


        // add token here with the header
        return chain.proceed(request.build())
    }

}