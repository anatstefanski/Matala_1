package com.example.first_exercise.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message ?: "Login failed") }
    }

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
    fun updateEmail(newEmail: String, onResult: (Boolean, String?) -> Unit) {

        val user = auth.currentUser

        user?.updateEmail(newEmail)
            ?.addOnSuccessListener {
                onResult(true, null)
            }
            ?.addOnFailureListener {
                onResult(false, it.message)
            }

    }

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

    fun reauthenticateAndUpdateEmail(
        currentEmail: String,
        password: String,
        newEmail: String,
        onResult: (Boolean, String?) -> Unit
    ) {

        val user = auth.currentUser ?: return

        val credential =
            com.google.firebase.auth.EmailAuthProvider
                .getCredential(currentEmail, password)

        user.reauthenticate(credential)
            .addOnSuccessListener {

                user.updateEmail(newEmail)
                    .addOnSuccessListener {
                        onResult(true, null)
                    }
                    .addOnFailureListener {
                        onResult(false, it.message)
                    }

            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }
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
}