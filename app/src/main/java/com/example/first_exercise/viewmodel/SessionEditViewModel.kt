package com.example.first_exercise.viewmodel

import androidx.lifecycle.ViewModel
import com.example.first_exercise.model.StudySession
import com.example.first_exercise.repository.SessionRepository

class SessionEditViewModel : ViewModel() {

    private val repo = SessionRepository()

    fun saveSession(session: StudySession, onResult: (Boolean) -> Unit) {
        repo.saveSession(session) { success ->
            onResult(success)
        }
    }
}
