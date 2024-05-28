package com.elis.orderingapplication.pojo2

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.squareup.moshi.Json
import javax.annotation.Nonnull

@Entity(
    tableName = "ordering_group",
    primaryKeys = ["order_group_id"]
)
data class OrderingGroup(
    @Nonnull
    @ColumnInfo(name = "order_group_id")
    //@Json(name = "orderingGroupNo")
    var orderingGroupNo: String,

    @Json(name = "orderingGroupDescription")
    var orderingGroupDescription: String?
)
