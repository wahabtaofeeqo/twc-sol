package com.wristband.sol.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "attendances")
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val aid: Int = 0,

    @ColumnInfo(name = "member_id")
    val member_id: Int,

    @ColumnInfo(name = "date")
    val date: Date
)