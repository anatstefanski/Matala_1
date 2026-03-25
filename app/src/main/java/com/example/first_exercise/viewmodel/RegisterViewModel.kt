package com.example.first_exercise.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.first_exercise.model.User
import com.example.first_exercise.repository.AuthRepository
import com.example.first_exercise.repository.UserRepository
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class RegisterViewModel : ViewModel() {
    // Connection to Firebase
    private val authRepo = AuthRepository()
    private val userRepo = UserRepository()

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    /**
     * Registers a new user
     * 1. Creates user in Firebase Authentication
     * 2. Saves user data in Firestore
     * 3. Updates UI state (Loading / Success / Error)
     */
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
                        authRepo.logout()
                        _registerState.value = RegisterState.Success
                    }.onFailure { e ->
                        _registerState.postValue(RegisterState.Error("Failed saving profile: ${e.message}"))
                    }
                }
            }
            res.onFailure { e ->
                val msg = if (e is FirebaseAuthUserCollisionException) {
                    "This email is already registered. Please log in."
                }else if (e.message?.contains("network", true) == true) {
                    "No internet connection"
                } else {
                    "Register failed: ${e.message}"
                }
                _registerState.postValue(RegisterState.Error(msg))
            }
        }
    }
}

/**
 * Represents registration state for the UI
 */
sealed class RegisterState {
    data object Loading : RegisterState()
    data object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}