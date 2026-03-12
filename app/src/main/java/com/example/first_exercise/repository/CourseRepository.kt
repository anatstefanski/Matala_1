package com.example.first_exercise.repository

import android.util.Log
import com.example.first_exercise.model.CourseItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class CourseRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val coursesRef = firestore.collection("courses")

    // הוספת קורס חדש (לשימוש Admin)
    fun addCourse(course: CourseItem, onComplete: (Boolean) -> Unit) {

        val courseId = coursesRef.document().id
        val courseWithId = course.copy(courseId = courseId)

        coursesRef.document(courseId)
            .set(courseWithId)
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }


    // שליפת קורסים בזמן אמת (Realtime updates)
    fun observeCourses(onResult: (List<CourseItem>) -> Unit): ListenerRegistration {

        return coursesRef.addSnapshotListener { snapshot, error ->

            if (error != null) {
                Log.e("COURSES", "Listen failed", error)
                onResult(emptyList())
                return@addSnapshotListener
            }

            val courses = snapshot
                ?.toObjects(CourseItem::class.java)
                ?: emptyList()

            Log.d("COURSES", "size = ${courses.size}")

            onResult(courses)
        }
    }
}
