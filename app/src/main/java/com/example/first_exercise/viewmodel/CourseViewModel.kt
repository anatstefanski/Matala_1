package com.example.first_exercise.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.first_exercise.model.CourseItem
import com.example.first_exercise.repository.CourseRepository

class CourseViewModel : ViewModel() {

    private val repo = CourseRepository()

    private val _allCourses = MutableLiveData<MutableList<CourseItem>>(mutableListOf())
    private val _filteredCourses = MutableLiveData<List<CourseItem>>()
    val filteredCourses: LiveData<List<CourseItem>> = _filteredCourses

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var currentSearchQuery: String = ""
    private var currentSelectedCategories: Set<String> = setOf("All")

    init {
        loadFirstCourses()
    }

    fun loadFirstCourses() {

        _isLoading.value = true

        repo.getFirstCourses { courses ->

            _allCourses.value = courses.toMutableList()

            _isLoading.postValue(false)

            applySearchAndFilter()
        }
    }

    fun loadMoreCourses() {

        repo.getMoreCourses { moreCourses ->

            val currentList = _allCourses.value ?: mutableListOf()

            currentList.addAll(moreCourses)

            _allCourses.postValue(currentList)

            applySearchAndFilter()
        }
    }

    fun updateSearchAndFilter(query: String? = null, categories: Set<String>? = null) {

        query?.let { currentSearchQuery = it }
        categories?.let { currentSelectedCategories = it }

        applySearchAndFilter()
    }

    private fun applySearchAndFilter() {

        var filtered = _allCourses.value ?: emptyList()

        if (!currentSelectedCategories.contains("All")) {

            filtered = filtered.filter {
                it.category in currentSelectedCategories
            }
        }

        if (currentSearchQuery.isNotEmpty()) {

            filtered = filtered.filter {
                it.title.contains(currentSearchQuery, true)
            }
        }

        _filteredCourses.value = filtered
    }
}