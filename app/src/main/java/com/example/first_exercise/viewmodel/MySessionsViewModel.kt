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
    val error = MutableLiveData<String>()
    private val _unreadCounts = MutableLiveData<Map<String, Int>>()
    val unreadCounts: LiveData<Map<String, Int>> = _unreadCounts
    val sessions = MutableLiveData<List<StudySession>>()
    val categoryCounts = MutableLiveData<Map<String, Int>>()

    // Default categories with zero values (used when no data exists)
    private val defaultCounts = linkedMapOf(
        "Computer Science" to 0,
        "Education" to 0,
        "Economics" to 0,
        "Behavioral Sciences" to 0
    )

    /**
     * Loads user sessions, calculates categories
     * and updates unread messages.
     */
    fun loadMySessions() {
        val userId = auth.currentUser?.uid ?: run {
            sessions.postValue(emptyList())
            categoryCounts.postValue(defaultCounts)
            return
        }

        attendanceRepo.getUserAttendances(userId) { attendanceList ->

            // If user has no sessions → show empty UI
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
                    if (error != null) {
                        this.error.postValue("Failed to load courses")
                        return@getCoursesByIds
                    }
                    val safeCourses = courses ?: emptyList()
                    // Map courseId → category
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

    /**
     * Calculates unread messages per session.
     *
     * Logic:
     * - For each session → fetch unread count from ChatRepository
     * - Aggregate results into a map
     */
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