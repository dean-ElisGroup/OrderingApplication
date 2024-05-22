package com.elis.orderingapplication.pojo2

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class OrderingGroup {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "order_group_id")
    val uId = 0
    @Json(name = "orderingGroupNo")
    var orderingGroupNo: String? = null

    @Json(name = "orderingGroupDescription")
    var orderingGroupDescription: String? = null
}
