package orderinfodatabasepojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class OrderInfo {
    @PrimaryKey(autoGenerate = true)
    val uId = 0

    @Json(name = "deliveryAddresses")
    var deliveryAddresses: List<DeliveryAddress>? = null

    @Json(name = "orderingGroups")
    var orderingGroups: List<OrderingGroup>? = null
}
