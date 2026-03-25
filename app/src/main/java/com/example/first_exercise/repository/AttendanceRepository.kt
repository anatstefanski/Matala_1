package com.example.first_exercise.repository

import com.example.first_exercise.model.Attendance
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Repository responsible for managing user attendance in sessions.
 * Handles enroll, unenroll, and retrieval of attendance data using Firebase Firestore.
 */
class AttendanceRepository {

    private val db = FirebaseFirestore.getInstance()
    private val attendanceCollection = db.collection("attendance")

    /**
     * Registers a user to a session.
     * Uses a unique ID (userId + sessionId) to prevent duplicate registrations.
     */
    fun enrollToSession(
        userId: String,
        sessionId: String,
        courseId: String,
        onComplete: (Boolean) -> Unit
    ) {

        val id = "${userId}_${sessionId}"

        val attendance = Attendance(
            attendanceId = id,
            userId = userId,
            sessionId = sessionId,
            courseId = courseId
        )

        attendanceCollection
            .document(id)
            .set(attendance)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    /**
     * Removes a user's registration from a session.
     * Uses batch delete for safe and efficient removal.
     */
    fun unenrollFromSession(
        userId: String,
        sessionId: String,
        onComplete: (Boolean) -> Unit
    ) {

        attendanceCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("sessionId", sessionId)
            .get()
            .addOnSuccessListener {
                val batch = db.batch()
                for(doc in it.documents){
                    batch.delete(doc.reference)
                }
                batch.commit()
                    .addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { onComplete(false) }
            }
    }

    /**
     * Retrieves all sessions that a specific user is registered to.
     */
    fun getUserAttendances(
        userId: String,
        onResult: (List<Attendance>) -> Unit
    ){
        attendanceCollection
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener {
                val list = it.toObjects(Attendance::class.java)
                onResult(list)
            }
    }
}