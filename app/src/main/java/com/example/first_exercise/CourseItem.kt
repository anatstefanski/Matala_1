package com.example.first_exercise

import java.io.Serializable

data class CourseItem(
    val title: String,
    val description: String,
    val category: String,
    val imageRes: Int,
    val videoUrl: String,
    val imageUrl: String? = null // new CourseItem
): Serializable
