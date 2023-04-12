package com.wristband.sol.data.api

import com.wristband.sol.data.BookingDTO
import com.wristband.sol.data.Response
import com.wristband.sol.data.model.Member
import com.wristband.sol.data.model.Ticket
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface EndpointsInterface {

    @GET("users")
    fun loadMembers(): Call<Response<List<Member>>>

    @POST("bookings")
    fun postTickets(@Body dto: BookingDTO): Call<Response<String>>
}