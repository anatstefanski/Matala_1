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

                // אם המייל או הסיסמה לא נכונים
                if (error?.contains("password", true) == true ||
                    error?.contains("credential", true) == true ||
                    error?.contains("user", true) == true) {

                    _loginState.postValue(LoginState.Error("Email or password is incorrect"))

                } else {

                    _loginState.postValue(LoginState.Error("Login failed"))

                }

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

    fun checkAdminForExistingUser(onResult: (Boolean) -> Unit) {
        val uid = repo.currentUid()
        if (uid != null) {
            repo.fetchIsAdmin(uid) { isAdmin ->
                onResult(isAdmin)
            }
        } else {
            onResult(false)
        }
    }
    fun getCurrentUserId(): String? {
        return repo.currentUid()
    }

}

sealed class LoginState {
    data object Loading : LoginState()
    data class Success(val isAdmin: Boolean) : LoginState()
    data class Error(val message: String) : LoginState()
}
