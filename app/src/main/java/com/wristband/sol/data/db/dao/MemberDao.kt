package com.wristband.sol.data.db.dao

import androidx.paging.DataSource
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.wristband.sol.data.model.Member

@Dao
interface MemberDao {

    @Query("SELECT * FROM members")
    fun getAll(): PagingSource<Int, Member>

    @Query("SELECT * FROM members LIMIT :limit OFFSET :offset")
    fun loadMembers(limit: Int, offset: Int): List<Member>

    @Query("SELECT * FROM members WHERE email = :email LIMIT 1")
    fun findByEmail(email: String): Member?

    @Query("SELECT * FROM members WHERE code = :code LIMIT 1")
    fun findByCode(code: String): Member?

    @Insert
    fun insert(member: Member)

    @Query("SELECT COUNT(id) FROM members")
    fun countAll(): Int

    @Insert
    fun insertAll(vararg members: Member)

    @Delete
    fun delete(member: Member)

    @Query("DELETE FROM members")
    fun deleteAll()
}