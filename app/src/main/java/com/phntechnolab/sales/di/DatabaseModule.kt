package com.phntechnolab.sales.di

import android.content.Context
import androidx.room.Room
import com.phntechnolab.sales.db.CustomDatabse
import com.phntechnolab.sales.model.UserData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun provideChannelDao(roomDatabase: CustomDatabse): UserData {
        return roomDatabase.getUserDetailsDao()
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext appContext: Context): CustomDatabse {
        return Room.databaseBuilder(
            appContext,
            CustomDatabse::class.java,
            "custom_DB"
        ).build()
    }
}