package com.wristband.sol.data.repositories

import com.wristband.sol.data.db.dao.UserDao
import com.wristband.sol.data.model.User
import javax.inject.Inject

class UserRepository @Inject constructor(private val dao: UserDao) {

    fun loadUsers(): List<User> {
        return dao.getAll()
    }

    fun insertUsers(vararg users: User) {
        dao.insertAll(*users)
    }
}