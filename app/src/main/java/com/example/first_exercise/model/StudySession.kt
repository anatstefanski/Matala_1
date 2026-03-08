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
    val zoomLink: String? = null,
    val participantsCount: Int = 0 ,// שדה לסטטיסטיקה: כמה סטודנטים נרשמו
    var isUserRegistered: Boolean = false // שדה מקומי לעדכון האדאפטר
)