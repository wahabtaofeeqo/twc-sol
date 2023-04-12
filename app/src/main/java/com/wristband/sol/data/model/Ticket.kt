package com.wristband.sol.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(tableName = "tickets")
data class Ticket(

    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    var tid: Int = 0,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    var name: String,

    @SerializedName("phone")
    @ColumnInfo(name = "phone")
    var phone: String,

    @SerializedName("access_type")
    @ColumnInfo(name = "access_type")
    var accessType: String,

    @SerializedName("access_quantity")
    @ColumnInfo(name = "access_quantity")
    var accessQuantity: Int,

    @SerializedName("cost")
    @ColumnInfo(name = "cost")
    var cost: Int,

    @SerializedName("package_type")
    @ColumnInfo(name = "package_type")
    var packageType: String? = null,

    @SerializedName("package_quantity")
    @ColumnInfo(name = "package_quantity")
    var packageQuantity: Int = 0,

    @SerializedName("cabana_type")
    @ColumnInfo(name = "cabana_type")
    var cabanaType: String? = null,

    @SerializedName("cabana_quantity")
    @ColumnInfo(name = "cabana_quantity")
    var cabanaQuantity: Int = 0,

    @SerializedName("date")
    @ColumnInfo(name = "date")
    val date: Date
)
