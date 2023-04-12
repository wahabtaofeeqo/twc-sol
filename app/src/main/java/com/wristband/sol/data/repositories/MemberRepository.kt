package com.wristband.sol.data.repositories

import androidx.paging.*
import com.wristband.sol.data.api.EndpointsInterface
import com.wristband.sol.data.db.dao.MemberDao
import com.wristband.sol.data.model.Member
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MemberRepository @Inject constructor(private val api: EndpointsInterface, private val dao: MemberDao) {

    fun loadMembersAPI() = api.loadMembers()

    fun loadMembers(limit: Int): Flow<PagingData<Member>> {
        return Pager(
            PagingConfig(
                pageSize = limit,
                enablePlaceholders = false,
                prefetchDistance = 3),
            pagingSourceFactory = { dao.getAll() }
        ).flow
    }

    fun insertAll(vararg members: Member) {
        return dao.insertAll(*members)
    }

    fun deleteAll() {
        dao.deleteAll()
    }

    fun findByCode(code: String): Member? {
        return dao.findByCode(code)
    }

    fun countAll() = dao.countAll()
}