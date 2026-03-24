package com.example.first_exercise.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.first_exercise.model.CourseItem
import com.example.first_exercise.repository.AttendanceRepository
import com.example.first_exercise.repository.ChatRepository
import com.example.first_exercise.repository.CourseRepository

class CourseViewModel : ViewModel() {
    // Connection to Firebase
    private val repo = CourseRepository()
    private val chatRepo = ChatRepository()
    private val attendanceRepo = AttendanceRepository()
    private val _totalUnreadCount = MutableLiveData<Int>()
    val totalUnreadCount: LiveData<Int> = _totalUnreadCount
    private val _allCourses = MutableLiveData<MutableList<CourseItem>>(mutableListOf())
    private val _filteredCourses = MutableLiveData<List<CourseItem>>()
    val filteredCourses: LiveData<List<CourseItem>> = _filteredCourses
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private var currentSearchQuery: String = ""
    private var currentSelectedCategories: Set<String> = setOf("All")
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _hasMore = MutableLiveData<Boolean>(true)
    val hasMore: LiveData<Boolean> = _hasMore

    init {
        loadFirstCourses()
    }
    /**
     * Loads the first page of courses from Firebase.
     */
    fun loadFirstCourses() {

        _isLoading.value = true

        repo.getFirstCourses { courses, error ->

            if (error != null) {
                _error.postValue("Failed to load courses")
                _isLoading.postValue(false)
                return@getFirstCourses
            }

            _allCourses.value = courses?.toMutableList() ?: mutableListOf()

            _isLoading.postValue(false)

            applySearchAndFilter()
        }
    }

    /**
     * Loads additional courses for pagination.
     */
    fun loadMoreCourses() {

        repo.getMoreCourses { moreCourses, error ->

            if (error != null) {
                _error.postValue("Failed to load more courses")
                return@getMoreCourses
            }

            val safeCourses = moreCourses ?: emptyList()

            if (safeCourses.isEmpty()) {
                _hasMore.postValue(false)
                return@getMoreCourses
            }

            val currentList = _allCourses.value ?: mutableListOf()

            currentList.addAll(safeCourses)

            _allCourses.postValue(currentList)

            applySearchAndFilter()
        }
    }

    /**
     * Updates search query and category filters.
     */
    fun updateSearchAndFilter(query: String? = null, categories: Set<String>? = null) {

        query?.let { currentSearchQuery = it }
        categories?.let { currentSelectedCategories = it }

        applySearchAndFilter()
    }

    /**
     * Applies filtering logic on the course list based on search and categories.
     */
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

    /**
     * Calculates total unread messages for all sessions the user is enrolled in.
     */
    fun loadTotalUnreadCount(userId: String) {
        attendanceRepo.getUserAttendances(userId) { attendanceList ->
            val sessionIds = attendanceList.map { it.sessionId }
            var total = 0
            var processed = 0
            if (sessionIds.isEmpty()) {
                _totalUnreadCount.postValue(0)
                return@getUserAttendances
            }
            sessionIds.forEach { sessionId ->
                chatRepo.getUnreadCount(userId, sessionId) { count ->
                    total += count
                    processed++
                    if (processed == sessionIds.size) {
                        _totalUnreadCount.postValue(total)
                    }
                }
            }
        }
    }
}