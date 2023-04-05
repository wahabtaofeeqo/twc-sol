package com.wristband.sol.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wristband.sol.data.db.dao.UserDao
import com.wristband.sol.data.model.User

@TypeConverters(Converters::class)
@Database(entities = [User::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "com.wristband.sol.db"
    }

    abstract fun userDao(): UserDao
}