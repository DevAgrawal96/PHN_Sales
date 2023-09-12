package com.phntechnolab.sales.di

import com.phntechnolab.sales.api.AuthInterceptor
import com.phntechnolab.sales.api.RetrofitApi
import com.phntechnolab.sales.util.Constants.TEST_URL1
import com.phntechnolab.sales.util.NullOnEmptyConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule
{
    @Singleton
    @Provides
    fun providesRetrofit() : Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .baseUrl(TEST_URL1)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Singleton
    @Provides
        fun providesLoginAPI(retrofitBuilder: Retrofit.Builder, okHttpClient: OkHttpClient) : RetrofitApi {
        return retrofitBuilder
            .client(okHttpClient).build().create(RetrofitApi::class.java)
    }
}