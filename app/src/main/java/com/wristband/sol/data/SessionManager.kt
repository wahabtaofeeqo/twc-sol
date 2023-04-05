package com.wristband.sol.data

import android.content.Context
import android.content.SharedPreferences
import com.wristband.sol.data.model.User

class SessionManager private constructor(private val context: Context) {

    private var editor: SharedPreferences.Editor? = null
    private var sharedPreferences: SharedPreferences? = null

    companion object {
        const val USERNAME = "name"
        const val USER_ID = "userId"
        const val LOGGED_IN = "loggedIn"
        const val NAME = "com.wristband.sol.session"

        @JvmStatic
        fun getInstance(context: Context) = SessionManager(context)
    }

    init {
        sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences!!.edit()
    }

    fun login(user: User) {
        editor?.let {
            it.putInt(USER_ID, user.uid)
            it.putBoolean(LOGGED_IN, true)
            it.putString(USERNAME, user.username)
            it.commit()
        }
    }

    fun logout() {
        editor?.let {
            it.remove(USER_ID)
            it.remove(USERNAME)
            it.remove(LOGGED_IN)
            it.commit()
        }
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences?.getBoolean(LOGGED_IN, false) ?: false
    }
}