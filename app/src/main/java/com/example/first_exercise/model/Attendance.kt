package com.example.first_exercise.model

data class Attendance(
    val attendanceId: String = "",
    val userId: String = "",
    val sessionId: String = "",
    val courseId: String = "",
    val lastReadTimestamp: Long = 0L
)
