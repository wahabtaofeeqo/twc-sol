package com.wristband.sol.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "members")
data class Member(
    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String?,

    @SerializedName("email")
    @ColumnInfo(name = "email")
    val email: String?,

    @SerializedName("category")
    @ColumnInfo(name = "category")
    val category: String?,

    @SerializedName("phone")
    @ColumnInfo(name = "phone")
    val phone: String?,

    @SerializedName("code")
    @ColumnInfo(name = "code")
    val code: String?,

    @SerializedName("expired_at")
    @ColumnInfo(name = "expired_at")
    val expiredAt: String?,
)