package com.wristband.sol.data.repositories

import com.wristband.sol.data.BookingDTO
import com.wristband.sol.data.LoginDTO
import com.wristband.sol.data.Result
import com.wristband.sol.data.api.EndpointsInterface
import com.wristband.sol.data.db.dao.UserDao
import com.wristband.sol.data.model.User
import javax.inject.Inject

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository @Inject constructor(private val api: EndpointsInterface, private val dao: UserDao) {

    fun login(username: String, password: String): Result<User> {

        val result: Result<User>
        val user = dao.findByUsername(username)
        result = if (user != null) {
            if(user.password == password) {
                Result.Success(user)
            } else Result.Error(Exception("Password not correct"))
        } else {
            Result.Error(Exception("Username not correct"))
        }

        return result
    }

    fun loginAPI(dto: LoginDTO) = api.login(dto)

}