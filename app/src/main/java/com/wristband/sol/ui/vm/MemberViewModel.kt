package com.wristband.sol.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.wristband.sol.data.Response as ApiResponse
import com.wristband.sol.data.model.Member
import com.wristband.sol.data.repositories.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.concurrent.thread

@HiltViewModel
class MemberViewModel @Inject constructor(private val repository: MemberRepository): ViewModel() {

    private val _apiResult = MutableLiveData<ApiResponse<List<Member>>>()
    val apiResult: LiveData<ApiResponse<List<Member>>> = _apiResult

    private val _clockIns = MutableLiveData<Int>()
    val clockIns: LiveData<Int> = _clockIns

    fun loadMembersAPI() {
        repository.loadMembersAPI().enqueue(object : Callback<ApiResponse<List<Member>>> {
            override fun onResponse(call: Call<ApiResponse<List<Member>>>, response: Response<ApiResponse<List<Member>>>) {
                _apiResult.value = response.body()
            }

            override fun onFailure(call: Call<ApiResponse<List<Member>>>, t: Throwable) {
                _apiResult.value = ApiResponse(false, "Unable to load data")
            }
        })
    }

    fun loadMembers(limit: Int): Flow<PagingData<Member>> {
        return repository.loadMembers(limit)
    }

    fun updateAll(members: List<Member>) {
        thread {
            repository.deleteAll()
            repository.insertAll(*members.toTypedArray())
        }
    }

    fun loadCount() {
        thread(true) {
            _clockIns.postValue(repository.countAll())
        }
    }
}