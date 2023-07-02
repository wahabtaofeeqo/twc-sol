package com.wristband.sol.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.wristband.sol.data.model.Member
import com.wristband.sol.data.model.Ticket

@Dao
interface TicketDao {
    @Query("SELECT * FROM tickets")
    fun getAll(): PagingSource<Int, Ticket>

    @Query("SELECT * FROM tickets")
    fun loadAll(): List<Ticket>

    @Insert
    fun insert(model: Ticket)

    @Insert
    fun insertAll(vararg models: Ticket)

    @Query("SELECT COUNT(tid) FROM tickets")
    fun countAll(): Int

    @Delete
    fun delete(model: Ticket)

    @Query("DELETE FROM tickets")
    fun deleteAll()

    @Query("SELECT * FROM tickets WHERE code = :code LIMIT 1")
    fun findByCode(code: String): Ticket?
}