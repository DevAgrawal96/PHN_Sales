package com.phntechnolab.sales.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
data class UserDataModel(
    val userId: Int,
    @PrimaryKey val id: Int,
    val name: String,
    val email: String,
    val mobile_no: String,
    val password: String,
    val role: String
): Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userId)
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(mobile_no)
        parcel.writeString(password)
        parcel.writeString(role)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserDataModel> {
        override fun createFromParcel(parcel: Parcel): UserDataModel {
            return UserDataModel(parcel)
        }

        override fun newArray(size: Int): Array<UserDataModel?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }
}