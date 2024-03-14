package com.elis.orderingapplication.pojo2

import com.squareup.moshi.Json
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class Order {
    @Json(name = "orderType")
    var orderType: String? = null

    @Json(name = "orderDate")
    var orderDate: String? = null

    @Json(name = "deliveryDate")
    var deliveryDate: String? = null

    @Json(name = "orderStatus")
    var orderStatus: Int? = null

    @Json(name = "articles")
    var articles: List<Article>? = null
}
