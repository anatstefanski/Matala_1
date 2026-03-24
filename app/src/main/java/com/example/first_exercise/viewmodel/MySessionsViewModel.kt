package com.example.first_exercise.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.first_exercise.model.StudySession
import com.example.first_exercise.repository.AttendanceRepository
import com.example.first_exercise.repository.ChatRepository
import com.example.first_exercise.repository.CourseRepository
import com.example.first_exercise.repository.SessionRepository
import com.google.firebase.auth.FirebaseAuth

class MySessionsViewModel : ViewModel() {

    private val attendanceRepo = AttendanceRepository()
    private val sessionRepo = SessionRepository()
    private val courseRepo = CourseRepository()
    private val auth = FirebaseAuth.getInstance()

    private val chatRepo = ChatRepository()
    private val _unreadCounts = MutableLiveData<Map<String, Int>>()

    val unreadCounts: LiveData<Map<String, Int>> = _unreadCounts

    val sessions = MutableLiveData<List<StudySession>>()
    val categoryCounts = MutableLiveData<Map<String, Int>>()

    private val defaultCounts = linkedMapOf(
        "Computer Science" to 0,
        "Education" to 0,
        "Economics" to 0,
        "Behavioral Sciences" to 0
    )

    fun loadMySessions() {
        val userId = auth.currentUser?.uid ?: run {
            sessions.postValue(emptyList())
            categoryCounts.postValue(defaultCounts)
            return
        }

        attendanceRepo.getUserAttendances(userId) { attendanceList ->

            if (attendanceList.isEmpty()) {
                sessions.postValue(emptyList())
                categoryCounts.postValue(defaultCounts)
                return@getUserAttendances
            }

            val sessionIds = attendanceList.map { it.sessionId }

            sessionRepo.getSessionsByIds(sessionIds) { sessionList ->

                if (sessionList.isEmpty()) {
                    sessions.postValue(emptyList())
                    categoryCounts.postValue(defaultCounts)
                    return@getSessionsByIds
                }
                loadUnreadCounts(userId, sessionList)

                val courseIds = sessionList.map { it.courseId }.distinct()

                courseRepo.getCoursesByIds(courseIds) { courses, error ->

                    // 🔴 טיפול בשגיאה
                    if (error != null) {
                        // אפשר להוסיף LiveData של error אם תרצי
                        return@getCoursesByIds
                    }

                    val safeCourses = courses ?: emptyList()

                    val courseIdToCategory =
                        safeCourses.associate { it.courseId to it.category }

                    val counts = defaultCounts.toMutableMap()

                    sessionList.forEach { session ->
                        val category = courseIdToCategory[session.courseId]

                        if (category != null && counts.containsKey(category)) {
                            counts[category] = counts[category]!! + 1
                        }
                    }

                    sessions.postValue(sessionList)
                    categoryCounts.postValue(counts)
                }
            }
        }
    }


    fun loadUnreadCounts(userId: String, sessions: List<StudySession>) {
        val counts = mutableMapOf<String, Int>()
        var processed = 0
        if (sessions.isEmpty()) {
            _unreadCounts.postValue(emptyMap())
            return
        }

        sessions.forEach { session ->
            chatRepo.getUnreadCount(userId, session.sessionId) { count ->
                counts[session.sessionId] = count
                processed++
                if (processed == sessions.size) {
                    _unreadCounts.postValue(counts)
                }
            }
        }
    }
}