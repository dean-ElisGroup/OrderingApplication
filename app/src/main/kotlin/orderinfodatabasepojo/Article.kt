package orderinfodatabasepojo

import com.squareup.moshi.Json
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class Article {
    @Json(name = "articleNo")
    var articleNo: String? = null

    @Json(name = "articleDescription")
    var articleDescription: String? = null

    @Json(name = "articleSize")
    var articleSize: String? = null

    @Json(name = "articleTargetQty")
    var articleTargetQty: Int? = null

    @Json(name = "articleMinQty")
    var articleMinQty: Int? = null

    @Json(name = "articleMaxQty")
    var articleMaxQty: Int? = null

    @Json(name = "articleIntervalQty")
    var articleIntervalQty: Int? = null
}
