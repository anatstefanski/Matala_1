package com.example.first_exercise.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.first_exercise.repository.AuthRepository

class LoginViewModel : ViewModel() {

    private val repo = AuthRepository()

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading
        repo.login(email, password) { success, error ->
            _loginState.postValue(
                if (success) LoginState.Success
                else LoginState.Error(error ?: "Login failed")
            )
        }
    }

    fun isLoggedIn(): Boolean = repo.isLoggedIn()


}

sealed class LoginState {
    data object Loading : LoginState()
    data object Success : LoginState()
    data class Error(val message: String) : LoginState()
}