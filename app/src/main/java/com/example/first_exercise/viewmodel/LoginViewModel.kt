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
            if (!success) {
                _loginState.postValue(LoginState.Error(error ?: "Login failed"))
                return@login
            }

            val uid = repo.currentUid()
            if (uid == null) {
                _loginState.postValue(LoginState.Error("Missing user uid"))
                return@login
            }

            repo.fetchIsAdmin(uid) { isAdmin ->
                _loginState.postValue(LoginState.Success(isAdmin))
            }
        }
    }

    fun isLoggedIn(): Boolean = repo.isLoggedIn()


}

sealed class LoginState {
    data object Loading : LoginState()
    data class Success(val isAdmin: Boolean) : LoginState()
    data class Error(val message: String) : LoginState()
}
