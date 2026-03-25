package com.example.first_exercise.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(), //Authentication
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance() //Database
) {


    /**
     * Logs in a user using Firebase Authentication.
     *
     * @param email User email
     * @param password User password
     * @param onResult Returns (success, errorMessage)
     */
    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message ?: "Login failed") }
    }

    /**
     * Registers a new user in Firebase.
     *
     * @param email User email
     * @param password User password
     * @param onResult Returns Result<uid> on success or failure
     */
    fun register(email: String, password: String, onResult: (Result<String>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { res ->
                val uid = res.user?.uid
                if (uid != null) onResult(Result.success(uid))
                else onResult(Result.failure(IllegalStateException("uid is null")))
            }
            .addOnFailureListener { e -> onResult(Result.failure(e)) }
    }

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun currentUid(): String? = auth.currentUser?.uid

    /**
     * Retrieves user role from Firestore.
     *
     * @param uid User ID
     * @param onResult Returns true if admin, otherwise false
     */
    fun fetchIsAdmin(uid: String, onResult: (Boolean) -> Unit) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val isAdmin = doc.getBoolean("admin") ?: false
                onResult(isAdmin)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    /**
     * Updates the password of the current user.
     *
     * @param newPassword New password
     * @param onResult Returns (success, errorMessage)
     */
    fun updatePassword(newPassword: String, onResult: (Boolean, String?) -> Unit) {

        val user = auth.currentUser

        user?.updatePassword(newPassword)
            ?.addOnSuccessListener {
                onResult(true, null)
            }
            ?.addOnFailureListener {
                onResult(false, it.message)
            }

    }

    /**
     * Sends verification before updating email.
     *
     * @param newEmail New email
     * @param onResult Returns (success, errorMessage)
     */
    fun verifyAndUpdateEmail(newEmail: String, onResult: (Boolean, String?) -> Unit) {

        val user = auth.currentUser

        user?.verifyBeforeUpdateEmail(newEmail)
            ?.addOnSuccessListener {
                onResult(true, null)
            }
            ?.addOnFailureListener {
                onResult(false, it.message)
            }

    }

    /**
     * Sends a password reset email.
     *
     * @param email User email
     * @param onResult Returns (success, errorMessage)
     */
    fun resetPassword(email: String, onResult: (Boolean, String?) -> Unit) {

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }
    fun logout() {
        auth.signOut()
    }

    /**
     * Retrieves user's name from Firestore.
     *
     * @param uid User ID
     * @param onResult Returns user name
     */
    fun getUserName(uid: String, onResult: (String) -> Unit) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("fullName") ?: "Student"
                onResult(name)
            }
            .addOnFailureListener {
                onResult("Student")
            }
    }
}