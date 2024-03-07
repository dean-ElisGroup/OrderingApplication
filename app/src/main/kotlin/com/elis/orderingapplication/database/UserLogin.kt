package com.elis.orderingapplication.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_login")
data class UserLogin(
    @PrimaryKey(autoGenerate = true)
    var userLoginId: Long = 0L,

    @ColumnInfo(name = "session_key")
    val sessionKey: String = "",

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "email")
    var email: String = "",

    @ColumnInfo(name = "message")
    var message: String = "",
)