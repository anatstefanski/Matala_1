package com.example.first_exercise.repository

import com.example.first_exercise.model.CourseItem
import com.google.firebase.firestore.FirebaseFirestore

class CourseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val coursesCollection = db.collection("courses")

    // הוספת קורס חדש (לשימוש המנהל)
    fun addCourse(course: CourseItem, onComplete: (Boolean) -> Unit) {
        val id = coursesCollection.document().id
        val courseWithId = course.copy(courseId = id)
        coursesCollection.document(id).set(courseWithId)
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    // שליפת כל הקורסים הקיימים (לצורך הצגה ב-Spinner או סינון)
    fun getAllCourses(onResult: (List<CourseItem>) -> Unit) {
        coursesCollection.get().addOnSuccessListener { snapshot ->
            val list = snapshot.toObjects(CourseItem::class.java)
            onResult(list)
        }
    }
}
