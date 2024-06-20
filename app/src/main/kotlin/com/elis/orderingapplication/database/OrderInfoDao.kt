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
import com.elis.orderingapplication.pojo2.JoinOrderingGroup
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.OrderRowsItem
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


    //@Query("UPDATE article SET solOrderQty = :newOrderQty, SolCountedQty = :countedQty WHERE articleNo = :articleId AND app_order_id = :appOrderId")
    @Query(
        """
    UPDATE article
    SET solOrderQty = CASE WHEN :newOrderQty = 0 AND :countedQty = 0 THEN NULL ELSE :newOrderQty END,
        SolCountedQty = CASE WHEN :newOrderQty = 0 AND :countedQty = 0 THEN NULL ELSE :countedQty END
    WHERE articleNo = :articleId AND app_order_id = :appOrderId
"""
    )
    fun updateOrderQty(newOrderQty: Int, articleId: String, appOrderId: String, countedQty: Int)

    @Query(
        """
    UPDATE pos_order SET appOrderStatus = :appOrderStatus where appOrderId = :appOrderId 
    """
    )
    fun updateOrderStatus(appOrderId: String, appOrderStatus: String)

    @Transaction
    @Query("SELECT  * FROM ordering_group WHERE order_group_id = :orderGroupId")
    fun getOrderingGroupDescription(orderGroupId: String): List<OrderingGroup>

    @Transaction
    @Query("SELECT deliveryAddressNo, orderingGroupNo, orderingGroupDescription FROM points_of_service WHERE deliveryAddressNo = :deliveryAddressNo GROUP BY orderingGroupNo")
    fun getOrderingGroupList(deliveryAddressNo: String): LiveData<List<JoinOrderingGroup>>

    @Transaction
    @Query("SELECT * FROM points_of_service WHERE deliveryAddressNo = :deliveryAddressNo AND orderingGroupNo = :orderingGroup ")
    fun getPointsOfService(deliveryAddressNo: String, orderingGroup: String) : LiveData<List<PointsOfService>>

    @Transaction
    @Query("SELECT * FROM pos_order WHERE deliveryAddressNo = :deliveryAddressNo AND point_of_service_no = :posNumber AND delivery_date = :deliveryDate AND orderStatus = :orderStatus ")
    fun getOrders(deliveryAddressNo: String, posNumber: String, deliveryDate: String, orderStatus: Int) : LiveData<List<Order>>

    @Transaction
    @Query("SELECT * FROM article WHERE order_date = :orderDate AND app_order_id = :appOrderId")
    fun getArticles(orderDate: String, appOrderId: String) : LiveData<List<Article>>

    @Transaction
    @Query("SELECT * FROM pos_order WHERE appOrderId = :appOrderId")
    fun getOrderByOrderId(appOrderId: String) : LiveData<List<Order>>

    @Transaction
    @Query("SELECT articleSize AS size, solOrderQty AS qty, articleNo FROM article WHERE delivery_date_article = :deliveryDate AND app_order_id = :appOrderId")
    fun getSendOrderArticles(deliveryDate: String?, appOrderId: String?): List<OrderRowsItem>
}