package com.wristband.sol.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wristband.sol.data.db.dao.AttendanceDao
import com.wristband.sol.data.model.Attendance
import com.wristband.sol.data.model.AttendanceWithMember
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AttendanceRepository @Inject constructor(val dao: AttendanceDao) {

    fun loadAttendances(limit: Int): Flow<PagingData<AttendanceWithMember>> {
        return Pager(
            PagingConfig(
                pageSize = limit,
                enablePlaceholders = false,
                prefetchDistance = 3),
            pagingSourceFactory = { dao.attendanceWithMember()}
        ).flow
    }

    fun getLastAttendance(memberId: Int): Attendance? {
        return dao.getLastAttendance(memberId)
    }

    fun create(attendance: Attendance) {
        return dao.insert(attendance)
    }

    fun getAllAttendance(): List<AttendanceWithMember> {
        return dao.getAllAttendanceWithMember()
    }
}