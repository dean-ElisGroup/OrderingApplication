package com.elis.orderingapplication.pojo2

import androidx.room.Entity

@Entity(primaryKeys = ["deliveryDate", "deliveryDate"])
data class OrderArticleJoin(
    val deliveryDate: String?,
    val deliveryDateArticle: String?
)