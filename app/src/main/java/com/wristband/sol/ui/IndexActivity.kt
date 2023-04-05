package com.wristband.sol.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.gson.Gson
import com.wristband.sol.data.SessionManager
import com.wristband.sol.data.model.User
import com.wristband.sol.data.repositories.UserRepository
import com.wristband.sol.ui.login.LoginActivity
import com.wristband.sol.ui.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.concurrent.thread

@AndroidEntryPoint
class IndexActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: UserRepository

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().setKeepOnScreenCondition { true }
        thread(start = true) {
            if(repository.loadUsers().isEmpty()) this.storeUser()
            else {
                if (sessionManager.isLoggedIn())
                    startActivity(Intent(this, WelcomeActivity::class.java))
                else
                    startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }
    }

    private fun storeUser() {
        try {
            val json = assets.open("users.json").bufferedReader().use { it.readText() }
            val user = Gson().fromJson(json, User::class.java)
            repository.insertUsers(user)
        }
        catch (_: Exception) {}

        //
        startActivity(Intent(this, LoginActivity::class.java))
    }
}