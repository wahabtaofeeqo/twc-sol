package com.wristband.sol.data.api

import com.wristband.sol.data.BookingDTO
import com.wristband.sol.data.LoginDTO
import com.wristband.sol.data.Response
import com.wristband.sol.data.model.Member
import com.wristband.sol.data.model.Ticket
import com.wristband.sol.data.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface EndpointsInterface {

    @GET("members")
    fun loadMembers(): Call<Response<List<Member>>>

    @POST("tickets")
    fun postTickets(@Body dto: BookingDTO): Call<Response<String>>

    @POST("agent-login")
    fun login(@Body dto: LoginDTO): Call<Response<User>>
}