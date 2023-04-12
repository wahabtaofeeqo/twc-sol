package com.wristband.sol.data.model

import androidx.room.ColumnInfo
import java.util.Date

data class AttendanceWithMember(
    @ColumnInfo(name = "aid")
    val aid: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "code")
    val code: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "date")
    val date: Date,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "expired_at")
    val expiredAt: String
)