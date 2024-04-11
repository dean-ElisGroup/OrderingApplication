package orderinfodatabasepojo

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
    // Additional json field to allow updating of order status when the ordering is being entered.
    @Json(name = "appOrderStatus")
    var appOrderStatus: String? = null

    @Json(name = "articles")
    var articles: List<Article>? = null
}
