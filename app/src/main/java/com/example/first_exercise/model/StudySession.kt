package com.example.first_exercise.model

data class StudySession(
    val sessionId: String = "",
    val courseId: String = "",
    val courseName: String = "",
    val topic: String = "",
    val date: String = "",
    val time: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationAddress: String = "",
    val locationSource: String = "" ,
    val zoomUrl: String? = null,
    val participantsCount: Int = 0 ,
    var isUserRegistered: Boolean = false
)