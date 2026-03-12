package com.example.first_exercise.repository

import com.example.first_exercise.model.Attendance
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AttendanceRepository {
    private val db = FirebaseFirestore.getInstance()

    // הרשמה למפגש (מבצע 2 פעולות: יוצר מסמך הרשמה ומעדכן מונה במפגש)
    fun enrollToSession(userId: String, sessionId: String, courseId: String, onComplete: (Boolean) -> Unit) {
        val attendanceId = "${userId}_${sessionId}"
        val attendance = Attendance(attendanceId, userId, sessionId, courseId)

        db.runTransaction { transaction ->
            // 1. הוספת רשומת נוכחות
            transaction.set(db.collection("attendance").document(attendanceId), attendance)

            // 2. עדכון מונה המשתתפים במפגש (בשביל הסטטיסטיקה)
            transaction.update(db.collection("sessions").document(sessionId), "participantsCount", FieldValue.increment(1))
        }.addOnCompleteListener { onComplete(it.isSuccessful) }
    }
    fun unenrollFromSession(userId: String, sessionId: String, onComplete: (Boolean) -> Unit) {
        val attendanceId = "${userId}_${sessionId}"
        db.collection("attendance").document(attendanceId)
            .delete()
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }
    // שליפת המפגשים אליהם הסטודנט נרשם
    fun getUserAttendances(userId: String, onResult: (List<Attendance>) -> Unit) {
        db.collection("attendance").whereEqualTo("userId", userId).get()
            .addOnSuccessListener { snapshot ->
                onResult(snapshot.toObjects(Attendance::class.java))
            }
    }
}