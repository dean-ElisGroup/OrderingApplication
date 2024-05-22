package com.elis.orderingapplication.pojo2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.squareup.moshi.Json
import javax.annotation.Generated
import javax.annotation.Nonnull

@Generated("jsonschema2pojo")
@Entity(tableName = "order",
        primaryKeys = ["delivery_date", "point_of_service_no"])

data class Order (
    @Json(name = "orderType")
    var orderType: String? = null,

    @Json(name = "orderDate")
    var orderDate: String? = null,

    @Nonnull
    @ColumnInfo(name = "delivery_date")
    @Json(name = "deliveryDate")
    var deliveryDate: String = "",// = null,

    @Json(name = "orderStatus")
    var orderStatus: Int? = null,
    // Additional json field to allow updating of order status when the ordering is being entered.
    @Json(name = "appOrderStatus")
    var appOrderStatus: String? = null,
    // Additional json field to allow updating of order status when the ordering is being entered.

    @Nonnull
    @ColumnInfo(name = "point_of_service_no")
    @Json(name = "appPointOfServiceNo")
    var appPosNo: String = "",

    @ColumnInfo(name = "pos_name")
    var posName: String? = "",

    @Json(name = "totalArticles")
    var totalArticles: Int? = null,

    //@Relation(parentColumn = "delivery_date",
    //    entityColumn = "delivery_date_article")
    @Ignore
    @Json(name = "articles")
    var articles: List<Article>? = null
)
{
    constructor(orderType: String, orderDate: String, deliveryDate: String,orderStatus: Int, appOrderStatus: String, appPosNo: String, totalArticles: Int) : this(orderType,orderDate,deliveryDate,orderStatus,appOrderStatus, appPosNo)
}
