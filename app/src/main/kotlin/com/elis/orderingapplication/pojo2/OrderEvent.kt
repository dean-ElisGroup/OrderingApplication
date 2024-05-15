package com.elis.orderingapplication.pojo2

import com.google.gson.annotations.SerializedName

data class OrderEvent(

	@field:SerializedName("sessionKey")
	val sessionKey: String? = null,

	@field:SerializedName("order")
	val order: SendOrder? = null
)

data class SendOrder(

	@field:SerializedName("pointOfServiceNo")
	val pointOfServiceNo: Int? = null,

	@field:SerializedName("deliveryAddressNo")
	val deliveryAddressNo: Int? = null,

	@field:SerializedName("externalOrderId")
	val externalOrderId: String? = null,

	@field:SerializedName("deliveryDate")
	val deliveryDate: String? = null,

	@field:SerializedName("orderReferenceId")
	val orderReferenceId: String? = null,

	@field:SerializedName("orderRows")
	val orderRows: List<OrderRowsItem?>? = null
)

data class OrderRowsItem(

	@field:SerializedName("size")
	val size: String? = null,

	@field:SerializedName("qty")
    var qty: Int? = null,

	@field:SerializedName("articleNo")
	var articleNo: String? = null
)
