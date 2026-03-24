package com.example.first_exercise.repository

import com.example.first_exercise.model.CourseItem
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class CourseRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val coursesRef = firestore.collection("courses")
    private val pageSize = 10
    private var lastVisible: DocumentSnapshot? = null

    /**
     * Fetches the first page of courses from Firebase.
     * Returns list of courses or an error message.
     */
    fun getFirstCourses(onResult: (List<CourseItem>?, String?) -> Unit) {

        coursesRef
            .orderBy("title")
            .limit(pageSize.toLong())
            .get()
            .addOnSuccessListener { snapshot ->

                if (!snapshot.isEmpty) {
                    lastVisible = snapshot.documents.last()
                }

                val courses = snapshot.toObjects(CourseItem::class.java)

                onResult(courses, null)
            }
            .addOnFailureListener { e ->
                onResult(null, e.message)
            }
    }

    /**
     * Fetches next page of courses for pagination.
     */
    fun getMoreCourses(onResult: (List<CourseItem>?, String?) -> Unit) {

        val last = lastVisible ?: run {
            onResult(emptyList(), null)
            return
        }

        coursesRef
            .orderBy("title")
            .startAfter(last)
            .limit(pageSize.toLong())
            .get()
            .addOnSuccessListener { snapshot ->

                if (!snapshot.isEmpty) {
                    lastVisible = snapshot.documents.last()
                }

                val courses = snapshot.toObjects(CourseItem::class.java)

                onResult(courses, null)
            }
            .addOnFailureListener { e ->
                onResult(null, e.message)
            }
    }

    /**
     * Adds a new course to Firestore.
     */
    fun addCourse(course: CourseItem, onComplete: (Boolean) -> Unit) {
        val courseId = coursesRef.document().id
        val courseWithId = course.copy(courseId = courseId)

        coursesRef.document(courseId)
            .set(courseWithId)
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    /**
     * Fetches courses by their IDs.
     */
    fun getCoursesByIds(ids: List<String>, onResult: (List<CourseItem>?, String?) -> Unit) {

        if (ids.isEmpty()) {
            onResult(emptyList(), null)
            return
        }

        coursesRef
            .whereIn("courseId", ids)
            .get()
            .addOnSuccessListener {
                val courses = it.toObjects(CourseItem::class.java)
                onResult(courses, null)
            }
            .addOnFailureListener { e ->
                onResult(null, e.message)
            }
    }
}