package com.example.first_exercise.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.first_exercise.model.User
import com.example.first_exercise.repository.AuthRepository
import com.example.first_exercise.repository.UserRepository

class EditProfileViewModel : ViewModel() {
    // Connection to Firebase
    private val userRepo = UserRepository()
    private val authRepo = AuthRepository()

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _updateResult = MutableLiveData<String>()
    val updateResult: LiveData<String> = _updateResult

    /**
     * Loads current user data from Firestore
     */
    fun loadUser() {

        // Get current user UID from Firebase Auth
        val uid = authRepo.currentUid() ?: return
        userRepo.getUser(uid) { result ->
            result.onSuccess {
                _user.postValue(it)
            }
        }
    }

    /**
     * Updates user profile
     * Handles different update scenarios: email, password, or name
     */
    fun updateUser(fullName: String, email: String, password: String) {

        val uid = authRepo.currentUid() ?: return
        val currentUser = user.value ?: return

        val updatedUser = User(
            uid = uid,
            fullName = fullName,
            email = email
        )

        // If email changed → update via Firebase Auth with verification
        if (email != currentUser.email) {

            // Send verification email before updating
            authRepo.verifyAndUpdateEmail(email) { emailSuccess, _ ->

                if (!emailSuccess) {
                    _updateResult.postValue("ERROR")
                    return@verifyAndUpdateEmail
                }

                // Update Firestore after email update
                userRepo.updateUser(updatedUser) {
                    _updateResult.postValue("EMAIL_CHANGED")
                }
            }
            return
        }

        // If password entered → update password in Firebase
        if (password.isNotEmpty()) {

            // Update password in Firebase Auth
            authRepo.updatePassword(password) { passSuccess, _ ->

                if (!passSuccess) {
                    _updateResult.postValue("ERROR")
                    return@updatePassword
                }

                userRepo.updateUser(updatedUser) {
                    _updateResult.postValue("PASSWORD_CHANGED")
                }
            }
            return
        }

        // Only name changed → update Firestore only
        userRepo.updateUser(updatedUser) {
            _updateResult.postValue("NAME_CHANGED")
        }
    }

    fun logout() {
        authRepo.logout()
    }

}