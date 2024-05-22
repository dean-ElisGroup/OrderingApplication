package com.elis.orderingapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.PointsOfService

@Dao
interface OrderInfoDao {

    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    //fun insert(orderInfo: ApiResponse<OrderInfo>?)

    //@Update
    //fun update(orderInfo: OrderInfo)

    //@Query("SELECT * FROM order_info")
    //fun getAll(): List<OrderInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deliveryAddress: List<DeliveryAddress>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPos(pointsOfService: List<PointsOfService>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(order: List<Order>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(order: List<Article>)

    @Update
    fun update(deliveryAddress: DeliveryAddress)

    @Query("SELECT * FROM delivery_address")
    fun getAll(): List<DeliveryAddress>





    //@Query("SELECT * from order_info WHERE orderInfoId = :key")
    //suspend fun get(key: Long): OrderInfo?

    //@Query("DELETE FROM order_info")
    //suspend fun clear()

    //@Query("SELECT * FROM order_info ORDER BY orderInfoId DESC")
    //fun getOrderInfo(): LiveData<List<OrderInfo>>

}