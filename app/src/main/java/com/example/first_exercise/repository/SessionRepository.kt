package com.example.first_exercise.repository

import com.example.first_exercise.model.StudySession
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SessionRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val sessionsCollection = db.collection("session") // ⚡ שם קולקציה

    fun getSessionsByCourse(courseId: String, onResult: (List<StudySession>) -> Unit) {
        val userId = auth.currentUser?.uid ?: ""
        sessionsCollection
            .whereEqualTo("courseId", courseId)
            .get()
            .addOnSuccessListener { sessionSnapshot ->
                val sessions = sessionSnapshot.toObjects(StudySession::class.java)
                db.collection("attendance")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("courseId", courseId)
                    .get()
                    .addOnSuccessListener { attendanceSnapshot ->
                        val registeredSessionIds = attendanceSnapshot.documents.map { it.getString("sessionId") }
                        sessions.forEach { session ->
                            session.isUserRegistered = registeredSessionIds.contains(session.sessionId)
                        }
                        onResult(sessions)
                    }
            }
    }

    fun createSession(session: StudySession, onComplete: (Boolean) -> Unit) {
        val id = sessionsCollection.document().id
        val sessionWithId = session.copy(sessionId = id)
        sessionsCollection.document(id).set(sessionWithId)
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }
}