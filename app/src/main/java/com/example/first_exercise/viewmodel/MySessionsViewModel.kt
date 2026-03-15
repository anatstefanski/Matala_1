package com.example.first_exercise.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData

import com.example.first_exercise.model.StudySession
import com.example.first_exercise.repository.AttendanceRepository
import com.example.first_exercise.repository.SessionRepository
import com.example.first_exercise.repository.CourseRepository

import com.google.firebase.auth.FirebaseAuth
class MySessionsViewModel : ViewModel() {

    private val attendanceRepo = AttendanceRepository()
    private val sessionRepo = SessionRepository()
    private val auth = FirebaseAuth.getInstance()
    private val courseRepo = CourseRepository()
    val sessions = MutableLiveData<List<StudySession>>()
    var categoryMap: Map<String, String> = emptyMap()
    fun loadMySessions() {

        val userId = auth.currentUser?.uid ?: return

        attendanceRepo.getUserAttendances(userId) { attendanceList ->

            val sessionIds = attendanceList.map { it.sessionId }

            sessionRepo.getSessionsByIds(sessionIds) { sessionList ->

                val courseIds = sessionList.map { it.courseId }.distinct()

                courseRepo.getCoursesByIds(courseIds) { courses ->

                    val categoryMap =
                        courses.associate { it.courseId to it.category }

                    val updatedSessions = sessionList.map { session ->

                        val category =
                            categoryMap[session.courseId] ?: "Unknown"

                        session.copy(
                            courseName = category
                        )
                    }

                    sessions.postValue(updatedSessions)
                }
            }
        }
    }
}