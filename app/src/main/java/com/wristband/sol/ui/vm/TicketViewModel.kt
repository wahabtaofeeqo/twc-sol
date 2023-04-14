package com.wristband.sol.ui.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.wristband.sol.data.Response
import com.wristband.sol.data.model.Ticket
import com.wristband.sol.data.repositories.TicketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import javax.inject.Inject
import kotlin.concurrent.thread

@HiltViewModel
class TicketViewModel @Inject constructor(private val repository: TicketRepository): ViewModel() {

    private val _createResponse = MutableLiveData<Response<Ticket>>()
    val createResponse: LiveData<Response<Ticket>> = _createResponse

    private val _postResponse = MutableLiveData<Response<String>>()
    val postResponse: LiveData<Response<String>> = _postResponse

    private val _bookings = MutableLiveData<Int>()
    val bookings: LiveData<Int> = _bookings

    fun createTicket(model: Ticket) {
        thread(true) {
            repository.create(model)
            _createResponse.postValue(Response(true, "Ticket created successfully", model))
        }
    }

    fun loadTickets(limit: Int): Flow<PagingData<Ticket>> {
        return repository.loadTickets(limit)
    }

    fun sendTicketsAPI() {
        thread(true) {
            repository.postTicketsAPI().enqueue(object : Callback<Response<String>> {
                override fun onResponse(call: Call<Response<String>>, response: retrofit2.Response<Response<String>>) {
                    if(response.isSuccessful) {
                        _postResponse.postValue(Response(true, "Bookings posted successfully"))
                    }
                    else {
                        _postResponse.postValue(Response(false, "Operation not succeeded"))
                    }
                }

                override fun onFailure(call: Call<Response<String>>, t: Throwable) {
                    _postResponse.postValue(Response(false, "Operation not succeeded"))
                }
            })
        }
    }

    fun loadCount() {
        thread(true) {
            _bookings.postValue(repository.countAll())
        }
    }
}