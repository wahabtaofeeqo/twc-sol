package com.wristband.sol.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import com.wristband.sol.data.LoginDTO
import com.wristband.sol.data.SessionManager
import com.wristband.sol.databinding.ActivityLoginBinding
import com.wristband.sol.ui.vm.LoginViewModel
import com.wristband.sol.ui.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(sessionManager.isLoggedIn()) this.toMain()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val login = binding.login
        val loading = binding.loading
        val username = binding.username
        val password = binding.password

        viewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        viewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }

            if (loginResult.success != null) {
                sessionManager.login(loginResult.success)
                toMain()
            }
        })

        username.afterTextChanged {
            viewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                viewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString())
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        viewModel.login(
                            username.text.toString(),
                            password.text.toString())
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                viewModel.loginAPI(
                    LoginDTO(
                        username.text.toString(), password.text.toString()
                    )
                )
            }
        }
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    private fun toMain() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}