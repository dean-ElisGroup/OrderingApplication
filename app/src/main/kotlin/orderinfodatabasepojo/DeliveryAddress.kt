package orderinfodatabasepojo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import javax.annotation.Generated

@Entity(tableName = "deliveryAddress")
data class DeliveryAddress (
    @PrimaryKey(autoGenerate = true)
    val uId: Long = 0,
    @ColumnInfo(name = "deliveryAddressNo")
    val deliveryAddressNo: String? = null,

    @ColumnInfo(name = "deliveryAddressName")
    val deliveryAddressName: String? = null

    //@Json(name = "pointsOfService")
    //var pointsOfService: List<PointsOfService>? = null
)
