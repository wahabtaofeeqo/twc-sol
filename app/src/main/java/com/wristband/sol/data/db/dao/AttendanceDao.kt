package com.wristband.sol.data.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.wristband.sol.data.model.Attendance
import com.wristband.sol.data.model.AttendanceWithMember

@Dao
interface AttendanceDao {

    @Query("SELECT * FROM attendances")
    fun findAll(): List<Attendance>

    @Query("SELECT * FROM attendances WHERE member_id = :memberId ORDER BY aid DESC LIMIT 1")
    fun getLastAttendance(memberId: Int): Attendance?

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM attendances INNER JOIN members ON members.id = member_id")
    fun attendanceWithMember(): PagingSource<Int, AttendanceWithMember>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM attendances INNER JOIN members ON members.id = member_id")
    fun getAllAttendanceWithMember(): List<AttendanceWithMember>

    @Insert
    fun insert(attendance: Attendance)

    @Delete
    fun delete(attendance: Attendance)
}