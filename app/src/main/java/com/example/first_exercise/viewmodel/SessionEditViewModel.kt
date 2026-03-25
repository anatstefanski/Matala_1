package com.example.first_exercise.viewmodel

import androidx.lifecycle.ViewModel
import com.example.first_exercise.model.StudySession
import com.example.first_exercise.repository.SessionRepository

/**
 * ViewModel that connects the UI with data operations.
 * Handles communication with external APIs (Geocoding & Reverse Geocoding)
 * and Firebase through the Repository layer.
 */
class SessionEditViewModel : ViewModel() {

    private val repo = SessionRepository()

    fun saveSession(session: StudySession, onResult: (Boolean) -> Unit) {
        repo.saveSession(session) { success ->
            onResult(success)
        }
    }

    fun searchLocationByQuery(
        query: String,
        onSuccess: (lat: Double, lon: Double, address: String) -> Unit,
        onError: (String) -> Unit
    ) {
        repo.searchLocationByQuery(query, onSuccess, onError)
    }

    fun reverseGeocode(
        lat: Double,
        lon: Double,
        onSuccess: (String) -> Unit,
        onError: () -> Unit
    ) {
        repo.reverseGeocode(lat, lon, onSuccess, onError)
    }
}