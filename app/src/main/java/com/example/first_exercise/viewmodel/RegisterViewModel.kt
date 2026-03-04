package com.example.first_exercise.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.first_exercise.model.User
import com.example.first_exercise.repository.AuthRepository
import com.example.first_exercise.repository.UserRepository
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class RegisterViewModel : ViewModel() {

    private val authRepo = AuthRepository()
    private val userRepo = UserRepository()

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    fun register(fullName: String, email: String, password: String) {
        _registerState.value = RegisterState.Loading

        authRepo.register(email, password) { res ->
            res.onSuccess { uid ->
                val appUser = User(
                    uid = uid,
                    fullName = fullName,
                    email = email,
                    admin = false
                )

                userRepo.createUser(appUser) { saveRes ->
                    saveRes.onSuccess {
                        _registerState.value = RegisterState.Success
                    }.onFailure { e ->
                        _registerState.postValue(RegisterState.Error("Failed saving profile: ${e.message}"))
                    }
                }
            }
            res.onFailure { e ->
                val msg = if (e is FirebaseAuthUserCollisionException) {
                    "This email is already registered. Please log in."
                } else {
                    "Register failed: ${e.message}"
                }
                _registerState.postValue(RegisterState.Error(msg))
            }
        }
    }
}

sealed class RegisterState {
    data object Loading : RegisterState()
    data object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}