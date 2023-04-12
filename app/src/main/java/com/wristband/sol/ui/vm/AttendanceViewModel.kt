package com.wristband.sol.ui.vm

import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.wristband.sol.data.Response
import com.wristband.sol.data.model.Attendance
import com.wristband.sol.data.model.AttendanceWithMember
import com.wristband.sol.data.repositories.AttendanceRepository
import com.wristband.sol.data.repositories.MemberRepository
import kotlinx.coroutines.flow.Flow
import java.io.OutputStream
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.thread

@HiltViewModel
class AttendanceViewModel @Inject constructor(private val repository: AttendanceRepository, private val memberRepository: MemberRepository): ViewModel() {

    private val _attendance = MutableLiveData<Response<Attendance?>>()
    val attendance: LiveData<Response<Attendance?>> = _attendance

    private val _exportResult = MutableLiveData<Response<Any>>()
    val exportResult: LiveData<Response<Any>> = _exportResult

    fun loadAttendance(limit: Int): Flow<PagingData<AttendanceWithMember>> {
        return repository.loadAttendances(limit)
    }

    fun verifyAndMark(code: String) {
        thread(start = true) {

            //
            val model = memberRepository.findByCode(code)
            if(model == null) {
                _attendance.postValue(Response(false,  "Member does not exist"))
                return@thread
            }

            // Check Attendance
            val attendance = repository.getLastAttendance(model.id)
            if(attendance == null) { // First time
                repository.create(Attendance(member_id = model.id, date = Date()))
                _attendance.postValue(Response( true, "Welcome ${model.name}"))
                return@thread
            }

            val todayDate = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(Date())
            val lastAttendance = attendance.date.let { SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(it) }

            if(todayDate == lastAttendance) {
                _attendance.postValue(Response(false, "${model.name}, You already clocked in Today"))
            }
            else {

                // Check for expiration
                val expireDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(model.expiredAt!!)
                if(System.currentTimeMillis() > expireDate!!.time) {
                    _attendance.postValue(Response(false, "Sorry! Your Card is expired. Contact Admin"))
                    return@thread
                }

                repository.create(Attendance(member_id = model.id, date = Date()))
                _attendance.postValue(Response(true, "Welcome ${model.name}. Date: $todayDate", attendance))
            }
        }
    }

    fun exportAttendanceToCSV(file: OutputStream) {

        thread(start = true) {
            try {
                var count = 0
                val printWriter = PrintWriter(file)
                for(user in repository.getAllAttendance()) {
                    count++
                    printWriter.println("${count}, ${user.name}, ${user.code}, ${user.date}, ${user.email}, ${user.phone}, ${user.expiredAt}")
                }

                //
                printWriter.close()
                _exportResult.postValue(Response(true, "Data exported successfully"))
            }
            catch (e: Exception) {
                _exportResult.postValue(Response( false, "Unable to export Data"))
            }
        }
    }
}