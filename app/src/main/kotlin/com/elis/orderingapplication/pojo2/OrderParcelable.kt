package com.elis.orderingapplication.pojo2

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

data class OrderParcelable(
    val orderType: String?,
    val orderDate: String?,
    val deliveryDate: String?,
    val orderStatus: Int?,
    val appOrderStatus: String?,
    val appPosNo: String,
    val posName: String?,
    val totalArticles: Int?,
    val deliveryAddressNo: String,
    val deliveryAddressName: String?,
    val appOrderId: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orderType)
        parcel.writeString(orderDate)
        parcel.writeString(deliveryDate)
        parcel.writeValue(orderStatus)
        parcel.writeValue(appOrderStatus)
        parcel.writeValue(appPosNo)
        parcel.writeValue(posName)
        parcel.writeValue(totalArticles)
        parcel.writeValue(deliveryAddressNo)
        parcel.writeValue(deliveryAddressName)
        parcel.writeString(appOrderId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderParcelable> {
        override fun createFromParcel(parcel: Parcel): OrderParcelable {
            return OrderParcelable(parcel)
        }

        override fun newArray(size: Int): Array<OrderParcelable?> {
            return arrayOfNulls(size)
        }
    }
}

