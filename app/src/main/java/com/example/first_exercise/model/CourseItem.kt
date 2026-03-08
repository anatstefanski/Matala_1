package com.example.first_exercise.model



data class CourseItem(
    val courseId: String="" ,
    val title: String="",
    val description: String="",
    val category: String="",
   // val imageRes: Int,
   // val videoUrl: String,
    val imageUrl: String? = "" // new CourseItem
)
{constructor() : this("", "", "", "", "")}