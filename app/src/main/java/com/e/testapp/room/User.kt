package com.e.testapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Int=0,
    @ColumnInfo(name = "userId") val userId: String?,
    @ColumnInfo(name = "userName") val userName: String?,
    @ColumnInfo(name = "xACC") val xACC: String?
)