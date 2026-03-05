package com.example.first_exercise.model

import java.io.Serializable

data class CourseItem(
    val courseId: String ,
    val title: String,
    val description: String,
    val category: String,
   // val imageRes: Int,
   // val videoUrl: String,
    val imageUrl: String? = null // new CourseItem
): Serializable