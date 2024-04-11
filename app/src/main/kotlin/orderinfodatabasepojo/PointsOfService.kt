package orderinfodatabasepojo

import com.squareup.moshi.Json
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class PointsOfService {
    @Json(name = "pointOfServiceNo")
    var pointOfServiceNo: String? = null

    @Json(name = "pointOfServiceName")
    var pointOfServiceName: String? = null

    @Json(name = "pointOfServiceDescription")
    var pointOfServiceDescription: String? = null

    @Json(name = "pointOfServiceOrderingGroupNo")
    var pointOfServiceOrderingGroupNo: String? = null

    @Json(name = "orders")
    var orders: List<Order>? = null
}
