package com.elis.orderingapplication.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.DelvPosJoin
import com.elis.orderingapplication.pojo2.JoinOrderingGroup
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.OrderingGroup
import com.elis.orderingapplication.pojo2.PointsOfService

@Dao
interface OrderInfoDao {
    // INSERTS
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deliveryAddress: List<DeliveryAddress>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPos(pointsOfService: List<PointsOfService>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(order: List<Order>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(order: List<Article>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrderingGroup(orderingGroup: List<OrderingGroup>)
    // UPDATES
    @Update
    fun update(deliveryAddress: DeliveryAddress)
    @Update
    fun updateOrder(order: Order)
    @Update
    fun updateArticle(article: Article)
    // QUERIES
    @Query("SELECT * FROM delivery_address")
    fun getAll(): LiveData<List<DeliveryAddress>>

    //@Query("SELECT * FROM delivery_address")
    //fun getAllOrderingGroups(): LiveData<List<OrderingGroup>>

    @Transaction
    @Query("SELECT  * FROM ordering_group WHERE order_group_id = :orderGroupId")
    fun getOrderingGroupDescription(orderGroupId: String): List<OrderingGroup>

    @Transaction
    @Query("SELECT deliveryAddressNo, orderingGroupNo, orderingGroupDescription FROM points_of_service WHERE deliveryAddressNo = :deliveryAddressNo GROUP BY orderingGroupNo")
    fun getOrderingGroupList(deliveryAddressNo: String): LiveData<List<JoinOrderingGroup>>

}