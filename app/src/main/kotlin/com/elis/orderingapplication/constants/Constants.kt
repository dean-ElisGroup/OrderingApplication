package com.elis.orderingapplication.constants
// Stores SOL Order status'
class Constants {
    companion object{
        const val ORDER_STATUS_NEW = 0
        const val ORDER_STATUS_STARTED = 1
        const val ORDER_STATUS_FINISHED = 3
        const val ORDER_STATUS_NO_DELIVERY = 4
        const val ORDER_STATUS_ORDER_TOO_LATE = 5
        const val ORDER_STATUS_NO_REQUIREMENT = 6

    }
}