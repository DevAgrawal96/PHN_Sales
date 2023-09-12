package com.phntechnolab.sales.api

import android.content.Context
import android.util.Log
import com.phntechnolab.sales.Modules.DataStoreProvider
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {

    @Inject
    lateinit var dataStoreProvider: DataStoreProvider

    var context: Context? = null

    public fun saveContext(context: Context) {
        this.context = context
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        // we can write log url here
        val request = chain.request().newBuilder()

        val token = context?.let { dataStoreProvider.getDataStoreInstance(it) }

        Log.d("TOken", token.toString())
        if(token != null) {
            request.addHeader("Authorization", "$token")
        }

        // add token here with the header
        return chain.proceed(request.build())
    }

}