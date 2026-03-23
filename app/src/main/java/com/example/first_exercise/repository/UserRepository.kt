package com.example.first_exercise.repository

import com.example.first_exercise.model.User
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()//Firestore
) {
    private val usersCol = db.collection("users")

    /**
     * Creates a new user document in Firestore
     *
     * @param user User object to save
     * @param onResult Callback with success or failure
     */
    fun createUser(user:User, onResult: (Result<Unit>) -> Unit) {
        usersCol.document(user.uid)
            .set(user)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { e -> onResult(Result.failure(e)) }
    }

    /**
     * Retrieves user data from Firestore
     *
     * @param uid User ID
     * @param onResult Returns User object or error
     */
    fun getUser(uid: String, onResult: (Result<User>) -> Unit) {
        usersCol.document(uid).get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(User::class.java)
                if (user != null) onResult(Result.success(user))
                else onResult(Result.failure(IllegalStateException("User profile not found")))
            }
            .addOnFailureListener { e -> onResult(Result.failure(e)) }
    }

    /**
     * Updates user fields in Firestore
     *
     * Only updates specific fields (fullName, email)
     * Does not overwrite the entire document
     */
    fun updateUser(user: User, onResult: (Result<Unit>) -> Unit) {
        usersCol.document(user.uid)
            .update(
                mapOf(
                    "fullName" to user.fullName,
                    "email" to user.email
                )
            )
            .addOnSuccessListener {
                onResult(Result.success(Unit))
            }
            .addOnFailureListener { e ->
                onResult(Result.failure(e))
            }
    }
}