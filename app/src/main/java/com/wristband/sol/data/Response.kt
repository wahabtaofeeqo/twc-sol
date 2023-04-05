package com.wristband.sol.data

data class Response<T>(
    val status: Boolean,
    val message: String,
    val data: T? = null
)
