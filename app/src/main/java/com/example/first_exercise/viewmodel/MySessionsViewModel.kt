package com.example.first_exercise.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData

import com.example.first_exercise.model.StudySession
import com.example.first_exercise.repository.AttendanceRepository
import com.example.first_exercise.repository.SessionRepository

import com.google.firebase.auth.FirebaseAuth
class MySessionsViewModel : ViewModel() {

    private val attendanceRepo = AttendanceRepository()
    private val sessionRepo = SessionRepository()
    private val auth = FirebaseAuth.getInstance()

    val sessions = MutableLiveData<List<StudySession>>()

    fun loadMySessions() {

        val userId = auth.currentUser?.uid ?: return

        attendanceRepo.getUserAttendances(userId) { attendanceList ->

            val sessionIds = attendanceList.map { it.sessionId }

            sessionRepo.getSessionsByIds(sessionIds) {

                sessions.postValue(it)

            }
        }
    }
}