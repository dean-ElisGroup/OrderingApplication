package com.elis.orderingapplication.pojo2

import androidx.room.Entity

@Entity(primaryKeys = ["pointOfService", "appPosNo"])
data class PosOrderJoin(
    val pointOfService: String?,
    val pointOfServiceOrder: String?
)