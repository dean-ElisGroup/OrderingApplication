package com.elis.orderingapplication.database


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_info")
data class OrderInfo(
    @PrimaryKey(autoGenerate = true)
    var orderInfoId: Long = 0L,

    @ColumnInfo(name = "delivery_address_name")
    val deliveryAddressName: String = "",

    @ColumnInfo(name = "delivery_address_number")
    val deliveryAddressNo: Int = 0,
)
