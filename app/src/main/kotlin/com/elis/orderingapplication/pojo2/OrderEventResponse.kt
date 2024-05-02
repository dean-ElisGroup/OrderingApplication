package com.elis.orderingapplication.pojo2

import com.google.gson.annotations.SerializedName

data class OrderEventResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("deliveryNoteNo")
	val deliveryNoteNo: Int? = null,

	@field:SerializedName("messages")
	val messages: List<String?>? = null,

	@field:SerializedName("orderResultRows")
	val orderResultRows: List<Any?>? = null,

	@field:SerializedName("externalOrderId")
	val externalOrderId: String? = null,

	@field:SerializedName("deliveryDate")
	val deliveryDate: String? = null
)
