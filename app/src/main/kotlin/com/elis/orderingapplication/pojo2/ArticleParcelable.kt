package com.elis.orderingapplication.pojo2

import android.os.Parcel
import android.os.Parcelable

data class ArticleParcelable(
    val articleNo: String,
    val articleDescription: String?,
    val articleSize: String?,
    val articleTargetQty: Int?,
    val articleMinQty: Int?,
    val articleMaxQty: Int?,
    val articleIntervalQty: Int?,
    val solOrderQty: Int?,
    val solCountedQty: Int?,
    val totalArticles: Int?,
    val pointOfService: String,
    val deliveryDate: String,
    val orderDate: String,
    val appOrderId: String,
    val deliveryAddressNo: String,
    val deliveryAddressName: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(articleNo)
        parcel.writeString(articleDescription)
        parcel.writeString(articleSize)
        parcel.writeValue(articleTargetQty)
        parcel.writeValue(articleMinQty)
        parcel.writeValue(articleMaxQty)
        parcel.writeValue(articleIntervalQty)
        parcel.writeValue(solOrderQty)
        parcel.writeValue(solCountedQty)
        parcel.writeValue(totalArticles)
        parcel.writeString(pointOfService)
        parcel.writeString(deliveryDate)
        parcel.writeString(orderDate)
        parcel.writeString(appOrderId)
        parcel.writeString(deliveryAddressNo)
        parcel.writeString(deliveryAddressName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ArticleParcelable> {
        override fun createFromParcel(parcel: Parcel): ArticleParcelable {
            return ArticleParcelable(parcel)
        }

        override fun newArray(size: Int): Array<ArticleParcelable?> {
            return arrayOfNulls(size)
        }
    }
}
