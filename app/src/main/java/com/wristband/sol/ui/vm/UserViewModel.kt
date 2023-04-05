package com.wristband.sol.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wristband.sol.data.Response
import com.wristband.sol.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.concurrent.thread

@HiltViewModel
class UserViewModel @Inject constructor(val repository: UserRepository) : ViewModel() {

    private val _usersAdded = MutableLiveData<Response<Boolean>>()
    val usersAdded: LiveData<Response<Boolean>> = _usersAdded

    fun checkUsers() {
        thread(start = true) {

        }
    }
}