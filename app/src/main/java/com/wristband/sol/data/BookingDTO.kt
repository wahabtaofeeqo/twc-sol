package com.wristband.sol.data

import com.google.gson.annotations.SerializedName
import com.wristband.sol.data.model.Ticket

data class BookingDTO(
    @SerializedName("bookings")
    val bookings: List<Ticket>
)
