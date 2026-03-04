package com.example.first_exercise.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val admin: Boolean = false
)