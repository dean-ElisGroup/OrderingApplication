package com.elis.orderingapplication.pojo2

import androidx.room.Embedded

data class PointsOfServiceWithTotalOrders(
    val totalPOS: Int,
    @Embedded val pointsOfService: PointsOfService,
    val totalOrders: Int
)