package com.elis.orderingapplication.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserLoginDao {

    @Insert
    suspend fun insert(night: UserLogin)

    @Update
    suspend fun update(night: UserLogin)

    @Query("SELECT * from user_login WHERE userLoginId = :key")
    suspend fun get(key: Long): UserLogin?

    @Query("DELETE FROM user_login")
    suspend fun clear()


    @Query("SELECT * FROM user_login ORDER BY userLoginId DESC")
    fun getAllNights(): LiveData<List<UserLogin>>

    @Query("SELECT * FROM user_login ORDER BY userLoginId DESC LIMIT 1")
    suspend fun getTonight(): UserLogin?

}