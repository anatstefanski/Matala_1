package com.example.first_exercise.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.first_exercise.model.StudySession
import com.example.first_exercise.repository.AttendanceRepository
import com.example.first_exercise.repository.SessionRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration

/**
 * ViewModel responsible for managing course sessions.
 *
 * Handles:
 * - Loading sessions for a course
 * - Tracking user registration status
 * - Registering/unregistering to sessions
 */
class SessionViewModel : ViewModel() {

    private val sessionRepo = SessionRepository()
    private val attendanceRepo = AttendanceRepository()
    private val auth = FirebaseAuth.getInstance()
    private val _sessions = MutableLiveData<List<StudySession>>()
    val sessions: LiveData<List<StudySession>> = _sessions
    val isLoading = MutableLiveData<Boolean>()
    private var listenerRegistration: ListenerRegistration? = null
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    /**
     * Loads all sessions for a given course and updates registration status.
     */
    fun loadSessionsForCourse(courseId: String) {

        isLoading.value = true
        listenerRegistration?.remove()

        val userId = auth.currentUser?.uid
        if (userId == null) {
            _errorMessage.postValue("User not logged in")
            isLoading.postValue(false)
            return
        }

        listenerRegistration = sessionRepo.observeSessionsByCourse(courseId) { sessions ->

            if (sessions.isEmpty()) {
                _sessions.postValue(emptyList())
                isLoading.postValue(false)
                return@observeSessionsByCourse
            }

            attendanceRepo.getUserAttendances(userId) { attendances ->

                val registeredIds = attendances.map { it.sessionId }

                val updatedSessions = sessions.map { session ->
                    session.copy(
                        isUserRegistered = registeredIds.contains(session.sessionId)
                    )
                }

                _sessions.postValue(updatedSessions)
                isLoading.postValue(false)
            }
        }
    }

    /**
     * Handles user registration/unregistration for a session.
     */
    fun toggleRegistration(session: StudySession, isRegistering: Boolean, courseId: String, isAdmin: Boolean) {

        // admin can't registration/unregistration
        if (isAdmin) {
            return
        }

        val userId = auth.currentUser?.uid ?: return
        if (isRegistering) {
            attendanceRepo.enrollToSession(userId, session.sessionId, courseId) { success ->
                if (success) {
                    updateSessionRegistration(session.sessionId, true)
                } else {
                    _errorMessage.postValue("Failed to register. Check your connection")
                }            }
        } else {
            attendanceRepo.unenrollFromSession(userId, session.sessionId) { success ->
                if (success) {
                    updateSessionRegistration(session.sessionId, false)
                } else {
                    _errorMessage.postValue("Failed to cancel registration")
                }            }
        }
    }

    /**
     * Updates local session list after registration change.
     */
    private fun updateSessionRegistration(sessionId: String, isRegistered: Boolean) {
        val updated = _sessions.value?.map {
            if (it.sessionId == sessionId)
                it.copy(isUserRegistered = isRegistered)
            else it
        }

        _sessions.postValue(updated ?: emptyList())
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
