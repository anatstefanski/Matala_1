package com.example.first_exercise.repository

import com.example.first_exercise.model.StudySession
import com.google.firebase.firestore.FirebaseFirestore

class SessionRepository {

    private val db = FirebaseFirestore.getInstance()
    private val sessionsCollection = db.collection("session")

    // Observable sessions by course
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

    // Create session
    fun saveSession(session: StudySession, onComplete: (Boolean) -> Unit) {

        val id = if (session.sessionId.isEmpty())
            sessionsCollection.document().id
        else
            session.sessionId

        val sessionWithId = session.copy(sessionId = id)

        sessionsCollection
            .document(id)
            .set(sessionWithId)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}

