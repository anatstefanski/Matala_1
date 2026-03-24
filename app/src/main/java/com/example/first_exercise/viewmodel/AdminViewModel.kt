package com.example.first_exercise.viewmodel

import androidx.lifecycle.ViewModel
import com.example.first_exercise.model.CourseItem
import com.example.first_exercise.repository.CourseRepository

class AdminViewModel : ViewModel() {

    private val repo = CourseRepository()
    /**
     * Saves a new course to Firebase.
     *
     * @param course The course data to save
     * @param onResult Callback indicating success or failure
     */
    fun saveCourse(course: CourseItem, onResult: (Boolean) -> Unit) {
        repo.addCourse(course, onResult)
    }

}
