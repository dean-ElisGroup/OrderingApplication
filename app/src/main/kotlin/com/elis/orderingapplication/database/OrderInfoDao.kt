package com.elis.orderingapplication.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface OrderInfoDao {

    @Insert
    suspend fun insert(orderInfo: OrderInfo)

    @Update
    suspend fun update(orderInfo: OrderInfo)

    @Query("SELECT * from order_info WHERE orderInfoId = :key")
    suspend fun get(key: Long): OrderInfo?

    @Query("DELETE FROM order_info")
    suspend fun clear()

    @Query("SELECT * FROM order_info ORDER BY orderInfoId DESC")
    fun getOrderInfo(): LiveData<List<OrderInfo>>

}