package com.example.first_exercise.model

data class Attendance(
    val attendanceId: String = "",   //
    val userId: String = "",         // מזהה הסטודנט
    val sessionId: String = "",      // מזהה המפגש
    val courseId: String = "",       //
    val lastReadTimestamp: Long = 0L
)
