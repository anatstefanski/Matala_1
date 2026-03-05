package com.example.first_exercise.repository

import com.example.first_exercise.model.StudySession
import com.google.firebase.firestore.FirebaseFirestore

class SessionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val sessionsCollection = db.collection("sessions")

    // יצירת מפגש חדש
    fun createSession(session: StudySession, onComplete: (Boolean) -> Unit) {
        val id = sessionsCollection.document().id
        val sessionWithId = session.copy(sessionId = id)
        sessionsCollection.document(id).set(sessionWithId)
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    // שליפת כל המפגשים או סינון לפי קורס
    fun getSessions(courseId: String? = null, onResult: (List<StudySession>) -> Unit) {
        val query = if (courseId != null) {
            sessionsCollection.whereEqualTo("courseId", courseId)
        } else {
            sessionsCollection
        }

        query.get().addOnSuccessListener { snapshot ->
            onResult(snapshot.toObjects(StudySession::class.java))
        }
    }
}