package orderinfodatabasepojo

import com.squareup.moshi.Json
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class OrderingGroup {
    @Json(name = "orderingGroupNo")
    var orderingGroupNo: String? = null

    @Json(name = "orderingGroupDescription")
    var orderingGroupDescription: String? = null
}
