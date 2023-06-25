package com.wristband.sol.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wristband.sol.data.BookingDTO
import com.wristband.sol.data.api.EndpointsInterface
import com.wristband.sol.data.db.dao.TicketDao
import com.wristband.sol.data.model.Member
import com.wristband.sol.data.model.Ticket
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TicketRepository @Inject constructor(private val api: EndpointsInterface, private val dao: TicketDao) {

    fun postTicketsAPI() = api.postTickets(BookingDTO(bookings = dao.loadAll()))

    fun create(model: Ticket) {
        return dao.insert(model)
    }

    fun loadTickets(limit: Int): Flow<PagingData<Ticket>> {
        return Pager(
            PagingConfig(
                pageSize = limit,
                enablePlaceholders = false,
                prefetchDistance = 3),
            pagingSourceFactory = { dao.getAll() }
        ).flow
    }

    fun countAll() = dao.countAll()

    fun findByCode(code: String): Ticket? {
        return dao.findByCode(code)
    }
}