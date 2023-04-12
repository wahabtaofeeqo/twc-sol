package com.wristband.sol.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wristband.sol.data.db.dao.UserDao
import com.wristband.sol.data.repositories.LoginRepository
import com.wristband.sol.ui.vm.LoginViewModel

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory(val dao: UserDao) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                loginRepository = LoginRepository(dao)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}