package com.wristband.sol.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wristband.sol.data.db.dao.AttendanceDao
import com.wristband.sol.data.db.dao.MemberDao
import com.wristband.sol.data.db.dao.TicketDao
import com.wristband.sol.data.db.dao.UserDao
import com.wristband.sol.data.model.Attendance
import com.wristband.sol.data.model.Member
import com.wristband.sol.data.model.Ticket
import com.wristband.sol.data.model.User

@TypeConverters(Converters::class)
@Database(entities = [User::class, Ticket::class,
    Member::class, Attendance::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "com.wristband.sol.db"
    }

    abstract fun userDao(): UserDao
    abstract fun ticketDao(): TicketDao
    abstract fun memberDao(): MemberDao
    abstract fun attendanceDao(): AttendanceDao
}