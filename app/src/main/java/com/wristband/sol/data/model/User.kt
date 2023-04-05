package com.wristband.sol.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val uid: Int,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "username")
    val username: String?,

    @ColumnInfo(name = "password")
    val password: String?
)
