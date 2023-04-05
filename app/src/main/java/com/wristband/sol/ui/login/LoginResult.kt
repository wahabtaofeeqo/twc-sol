package com.wristband.sol.ui.login

import com.wristband.sol.data.model.User
import com.wristband.sol.ui.login.LoggedInUserView

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: User? = null,
    val error: Int? = null
)