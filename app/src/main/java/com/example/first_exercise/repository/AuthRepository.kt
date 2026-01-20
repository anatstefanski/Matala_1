package com.example.first_exercise.repository

import com.google.firebase.auth.FirebaseAuth

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun logout() {
        auth.signOut()
    }
}