package com.elis.orderingapplication.database

class UserLoginInsert(private val userLoginDao: UserLoginDao) {
    suspend fun insert(userDb: UserLogin) {
        userLoginDao.insert(userDb)
    }

}
