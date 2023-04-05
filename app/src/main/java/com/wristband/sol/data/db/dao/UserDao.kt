package com.wristband.sol.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.wristband.sol.data.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): List<User>

    @Query("SELECT * FROM users WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM users WHERE name = :name LIMIT 1")
    fun findByName(name: String): User

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    fun findByUsername(username: String): User?

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)
}