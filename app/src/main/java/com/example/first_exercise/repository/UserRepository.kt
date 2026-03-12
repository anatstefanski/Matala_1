package com.example.first_exercise.repository

import com.example.first_exercise.model.User
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCol = db.collection("users")

    fun createUser(user:User, onResult: (Result<Unit>) -> Unit) {
        usersCol.document(user.uid)
            .set(user)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { e -> onResult(Result.failure(e)) }
    }

    fun getUser(uid: String, onResult: (Result<User>) -> Unit) {
        usersCol.document(uid).get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(User::class.java)
                if (user != null) onResult(Result.success(user))
                else onResult(Result.failure(IllegalStateException("User profile not found")))
            }
            .addOnFailureListener { e -> onResult(Result.failure(e)) }
    }
    fun updateUser(user: User, onResult: (Result<Unit>) -> Unit) {

        usersCol.document(user.uid)
            .set(user)
            .addOnSuccessListener {

                onResult(Result.success(Unit))

            }
            .addOnFailureListener { e ->

                onResult(Result.failure(e))

            }
    }
}