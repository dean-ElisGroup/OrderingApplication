package com.elis.orderingapplication.pojo2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.squareup.moshi.Json
import javax.annotation.Generated
import javax.annotation.Nonnull

@Entity(tableName = "article",
    primaryKeys = ["articleNo","delivery_date_article", "order_date", "point_of_service", "deliveryAddressNo"])
data class Article (
    @Nonnull
    @Json(name = "articleNo")
    //var articleNo: String? = null,
    var articleNo: String = "",

    @Json(name = "articleDescription")
    var articleDescription: String? = null,

    @Json(name = "articleSize")
    var articleSize: String? = null,

    @Json(name = "articleTargetQty")
    var articleTargetQty: Int? = null,

    @Json(name = "articleMinQty")
    var articleMinQty: Int? = null,

    @Json(name = "articleMaxQty")
    var articleMaxQty: Int? = null,

    @Json(name = "articleIntervalQty")
    var articleIntervalQty: Int? = null,

    @Json(name = "solOrderQty")
    var solOrderQty: Int? = 0,

    @Json(name = "solCountedQty")
    var solCountedQty: Int? = null,

    @ColumnInfo(name = "totalArticles")
    var totalArticles: Int? = null,

    @Nonnull
    @ColumnInfo(name = "point_of_service")
    var pointOfService: String = "",

    @Nonnull
    @ColumnInfo(name = "delivery_date_article")
    var deliveryDate: String = "",

    @Nonnull
    @ColumnInfo(name = "order_date")
    var orderDate: String = "",

    @ColumnInfo(name = "app_order_id")
    var appOrderId: String = "",

    @Nonnull
    @ColumnInfo(name = "deliveryAddressNo")
    var deliveryAddressNo: String = "",

    @ColumnInfo(name = "deliveryAddressName")
    var deliveryAddressName: String? = ""
)



