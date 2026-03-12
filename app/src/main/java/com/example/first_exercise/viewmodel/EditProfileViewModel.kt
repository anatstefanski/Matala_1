package com.example.first_exercise.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.first_exercise.model.User
import com.example.first_exercise.repository.AuthRepository
import com.example.first_exercise.repository.UserRepository

class EditProfileViewModel : ViewModel() {

    private val userRepo = UserRepository()
    private val authRepo = AuthRepository()

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _updateResult = MutableLiveData<String>()
    val updateResult: LiveData<String> = _updateResult

    fun loadUser() {

        val uid = authRepo.currentUid() ?: return

        userRepo.getUser(uid) { result ->

            result.onSuccess {
                _user.postValue(it)
            }

        }
    }

    fun updateUser(fullName: String, email: String, password: String) {

        val uid = authRepo.currentUid() ?: return
        val currentUser = user.value ?: return

        val updatedUser = User(
            uid = uid,
            fullName = fullName,
            email = email
        )

        // שינוי מייל
        if (email != currentUser.email) {

            authRepo.verifyAndUpdateEmail(email) { emailSuccess, _ ->

                if (!emailSuccess) {
                    _updateResult.postValue("ERROR")
                    return@verifyAndUpdateEmail
                }

                userRepo.updateUser(updatedUser) {
                    _updateResult.postValue("EMAIL_CHANGED")
                }

            }

            return
        }

        // שינוי סיסמה בלבד
        if (password.isNotEmpty()) {

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

        // שינוי שם בלבד
        userRepo.updateUser(updatedUser) {
            _updateResult.postValue("NAME_CHANGED")
        }
    }



    fun logout() {
        authRepo.logout()
    }

}