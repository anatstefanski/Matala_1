package com.example.first_exercise.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.first_exercise.model.CourseItem
import com.example.first_exercise.repository.CourseRepository

class CourseViewModel : ViewModel() {
    private val repository = CourseRepository()

    // רשימת הקורסים שתצוגת ה-Activity תאזין לה
    private val _courses = MutableLiveData<List<CourseItem>>()
    val courses: LiveData<List<CourseItem>> = _courses

    // מצב טעינה להצגת Progress Bar
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadCourses() {
        _isLoading.value = true
        repository.getAllCourses { list ->
            _courses.postValue(list)
            _isLoading.postValue(false)
        }
    }
}