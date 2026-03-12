package com.example.first_exercise.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.first_exercise.model.StudySession
import com.example.first_exercise.repository.AttendanceRepository
import com.example.first_exercise.repository.SessionRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration

class SessionViewModel : ViewModel() {

    private val sessionRepo = SessionRepository()
    private val attendanceRepo = AttendanceRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _sessions = MutableLiveData<List<StudySession>>()
    val sessions: LiveData<List<StudySession>> = _sessions

    val isLoading = MutableLiveData<Boolean>()
    private var listenerRegistration: ListenerRegistration? = null

    fun loadSessionsForCourse(courseId: String) {
        isLoading.value = true
        listenerRegistration?.remove()

        listenerRegistration = sessionRepo.observeSessionsByCourse(courseId) { list ->
            _sessions.postValue(list)
            isLoading.postValue(false)
        }
    }
    fun toggleRegistration(session: StudySession, isRegistering: Boolean, courseId: String) {
        val userId = auth.currentUser?.uid ?: return

        if (isRegistering) {
            attendanceRepo.enrollToSession(userId, session.sessionId, courseId) { success ->
                if (success) updateSessionRegistration(session.sessionId, true)
            }
        } else {
            attendanceRepo.unenrollFromSession(userId, session.sessionId) { success ->
                if (success) updateSessionRegistration(session.sessionId, false)
            }
        }
    }
    private fun updateSessionRegistration(sessionId: String, isRegistered: Boolean) {
        val updated = _sessions.value?.map {
            if (it.sessionId == sessionId) it.copy(isUserRegistered = isRegistered) else it
        }
        _sessions.postValue(updated)
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
