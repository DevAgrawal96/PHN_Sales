package com.phntechnolab.sales.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.phntechnolab.sales.model.UserData
import com.phntechnolab.sales.model.UserDataModel

@Database(entities = [UserDataModel::class], version = 1,exportSchema = false )
abstract class CustomDatabse: RoomDatabase() {
    abstract fun getUserDetailsDao(): UserData

}