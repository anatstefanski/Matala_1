package com.example.first_exercise.repository

import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
//It is responsible for- Sending HTTP requests, Receiving responses from the internet (API)
import com.android.volley.toolbox.Volley
import com.example.first_exercise.App
import com.example.first_exercise.model.StudySession
import com.google.firebase.firestore.FirebaseFirestore
import java.net.URLEncoder

class SessionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val sessionsCollection = db.collection("session")

    /**
     * Listens in real-time to all sessions of a specific course from Firebase.
     * Updates the UI whenever data changes.
     */
    fun observeSessionsByCourse(courseId: String, onResult: (List<StudySession>) -> Unit) =
        sessionsCollection
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val sessions = snapshot.toObjects(StudySession::class.java)
                onResult(sessions)
            }

    /**
     * Saves a session to Firebase Firestore.
     * Creates a new ID if needed and returns success/failure result.
     */
    fun saveSession(session: StudySession, onComplete: (Boolean) -> Unit) {
        val id = if (session.sessionId.isEmpty()) {
            sessionsCollection.document().id
        } else {
            session.sessionId
        }

        val sessionWithId = session.copy(sessionId = id)

        sessionsCollection
            .document(id)
            .set(sessionWithId)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getSessionsByIds(ids: List<String>, onResult: (List<StudySession>) -> Unit) {
        if (ids.isEmpty()) {
            onResult(emptyList())
            return
        }

        db.collection("session")
            .whereIn("sessionId", ids)
            .get()
            .addOnSuccessListener {
                val sessions = it.toObjects(StudySession::class.java)
                onResult(sessions)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    /**
     * Sends a location query (address) to an external API (OpenStreetMap),
     * converts it to latitude & longitude (Geocoding),
     * and returns the result or an error message.
     */
    fun searchLocationByQuery(
        query: String,
        onSuccess: (lat: Double, lon: Double, address: String) -> Unit,
        onError: (String) -> Unit
    ) {
        //Uses the OpenStreetMap Nominatim API to search for a location
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = "https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=jsonv2&limit=1"

        // Sending a GET request
        val request = object : JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                if (response.length() == 0) {
                    onError("Location not found")
                } else {
                    val firstResult = response.getJSONObject(0)
                    val lat = firstResult.optString("lat", "").toDoubleOrNull()
                    val lon = firstResult.optString("lon", "").toDoubleOrNull()
                    val displayName = firstResult.optString("display_name", "")

                    if (lat == null || lon == null) {
                        onError("Invalid location data")
                    } else {
                        onSuccess(lat, lon, displayName)
                    }
                }
            },
            {
                onError("Failed to search location")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("User-Agent" to "SmartGroupStudentProject/1.0")
            }
        }
        //Sends the request to the internet
        Volley.newRequestQueue(App.instance).add(request)
    }

    /**
     * Converts latitude & longitude into a readable address
     * using an external API (Reverse Geocoding).
     */
    fun reverseGeocode(
        lat: Double,
        lon: Double,
        onSuccess: (String) -> Unit,
        onError: () -> Unit
    ) {
        val url = "https://nominatim.openstreetmap.org/reverse?lat=$lat&lon=$lon&format=jsonv2"

        val request = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                val address = response.optString("display_name", "$lat, $lon")
                onSuccess(address)
            },
            {
                onError()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("User-Agent" to "SmartGroupStudentProject/1.0")
            }
        }

        Volley.newRequestQueue(App.instance).add(request)
    }
}