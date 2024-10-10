package com.elis.orderingapplication.utils

import com.elis.orderingapplication.pojo2.OrderEventResponse

sealed class ApiResponse<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T?) : ApiResponse<T>(data)
    //class Success<T>(data: T?) : ApiResponse<OrderEventResponse>(OrderEventResponse())
    class Error<T>(message: String, data: T? = null) : ApiResponse<T>(data, message)
    class Loading<T> : ApiResponse<T>()
    class ErrorLogin<T>(message: String, data: T? = null) : ApiResponse<T>(data, message)
    class ErrorSendOrderDate<T>(message: String, data: T? = null) : ApiResponse<T>(data, message)
    class NoDataError<T>(message: String, data: T? = null) : ApiResponse<T>(data, message)
    class UnknownError<T>(message: String, data: T? = null) : ApiResponse<T>(data, message)
    class NetworkError<T>(message: String, data: T? = null) : ApiResponse<T>(data, message)
}