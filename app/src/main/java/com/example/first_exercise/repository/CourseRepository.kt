package com.example.first_exercise.repository

import com.example.first_exercise.model.CourseItem
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class CourseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val courseCollection = db.collection("course")


    private val firestore = FirebaseFirestore.getInstance()
    private val coursesRef = firestore.collection("courses")

    private val pageSize = 10
    private var lastVisible: DocumentSnapshot? = null

    fun getFirstCourses(onResult: (List<CourseItem>) -> Unit) {

        coursesRef
            .orderBy("title")
            .limit(pageSize.toLong())
            .get()
            .addOnSuccessListener { snapshot ->

                if (!snapshot.isEmpty) {
                    lastVisible = snapshot.documents[snapshot.size() - 1]
                }

                val courses = snapshot.toObjects(CourseItem::class.java)

                onResult(courses)
            }
    }

    fun getMoreCourses(onResult: (List<CourseItem>) -> Unit) {

        val last = lastVisible ?: return

        coursesRef
            .orderBy("title")
            .startAfter(last)
            .limit(pageSize.toLong())
            .get()
            .addOnSuccessListener { snapshot ->

                if (!snapshot.isEmpty) {
                    lastVisible = snapshot.documents[snapshot.size() - 1]
                }

                val courses = snapshot.toObjects(CourseItem::class.java)

                onResult(courses)
            }
    }

    fun addCourse(course: CourseItem, onComplete: (Boolean) -> Unit) {

        val courseId = coursesRef.document().id
        val courseWithId = course.copy(courseId = courseId)

        coursesRef.document(courseId)
            .set(courseWithId)
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    fun getCoursesByIds(ids: List<String>, onResult: (List<CourseItem>) -> Unit) {

        if(ids.isEmpty()){
            onResult(emptyList())
            return
        }

        courseCollection
            .whereIn("courseId", ids)
            .get()
            .addOnSuccessListener {

                val courses = it.toObjects(CourseItem::class.java)

                onResult(courses)
            }
    }
}