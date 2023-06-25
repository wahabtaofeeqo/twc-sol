package com.wristband.sol.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.wristband.sol.R
import com.wristband.sol.data.LoginDTO
import com.wristband.sol.data.Response
import com.wristband.sol.data.repositories.LoginRepository
import com.wristband.sol.data.Result
import com.wristband.sol.data.model.User
import com.wristband.sol.ui.login.LoginFormState
import com.wristband.sol.ui.login.LoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import javax.inject.Inject
import kotlin.concurrent.thread

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        thread(true) {
            val result = repository.login(username, password)
            if (result is Result.Success) {
                _loginResult.postValue(LoginResult(success = result.data))
            } else {
                _loginResult.postValue(LoginResult(error = R.string.login_failed))
            }
        }
    }

    fun loginAPI(dto: LoginDTO) {
        thread(true) {
            repository.loginAPI(dto).enqueue(object : Callback<Response<User>> {
                override fun onResponse(call: Call<Response<User>>, response: retrofit2.Response<Response<User>>) {
                    if(response.isSuccessful) {
                        val data = response.body()?.data
                        _loginResult.postValue(LoginResult(success = data))
                    }
                    else {
                        _loginResult.postValue(LoginResult(error = R.string.login_failed))
                    }
                }

                override fun onFailure(call: Call<Response<User>>, t: Throwable) {
                    _loginResult.postValue(LoginResult(error = R.string.login_failed))
                }
            })
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}