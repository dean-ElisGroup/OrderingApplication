package com.elis.orderingapplication.interfaces

sealed class BaseResponse<out T>{
    data class Success<out T>(val data: T? = null) : BaseResponse<T>()
    data class Loading(val nothing: Nothing?=null) : BaseResponse<Nothing>()
    data class Error(val msg: String?) : BaseResponse<Nothing>()
    data class ErrorLogin<out T>(val data: T? = null) : BaseResponse<T>()
    data class NoDataError(val msg: String?) : BaseResponse<Nothing>()
}
