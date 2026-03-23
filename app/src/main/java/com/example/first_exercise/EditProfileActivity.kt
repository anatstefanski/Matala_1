package com.example.first_exercise

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.first_exercise.viewmodel.EditProfileViewModel
import com.google.android.material.button.MaterialButton

class EditProfileActivity : AppCompatActivity() {

    private val viewModel: EditProfileViewModel by viewModels()


    //Store original values to detect if user made changes
    private var originalName = ""
    private var originalEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Link to XML
        setContentView(R.layout.activity_editprofile)

        val username = findViewById<EditText>(R.id.et_username)
        val email = findViewById<EditText>(R.id.et_email)
        val password = findViewById<EditText>(R.id.et_password)
        val saveBtn = findViewById<MaterialButton>(R.id.save_changes_btn)
        val logout = findViewById<TextView>(R.id.logout_text)

        // Load current user data from Firebase
        viewModel.loadUser()

        /**
         * Observe user data from ViewModel
         * Populate input fields with current user information
         * Store original values for change detection
         */
        viewModel.user.observe(this) { user ->

            username.setText(user.fullName)
            email.setText(user.email)

            originalName = user.fullName
            originalEmail = user.email

        }

        /**
         * Handle save button click
         * Validate input and send update request
         */
        saveBtn.setOnClickListener {
            val newName = username.text.toString().trim()
            val newEmail = email.text.toString().trim()
            val newPassword = password.text.toString()

            // Check if no changes were made
            if (newName == originalName &&
                newEmail == originalEmail &&
                newPassword.isEmpty()) {

                Toast.makeText(this, "No changes were made", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if no changes were made
            val nameRegex = Regex("^[A-Za-z]{1,15}$")

            // Validate full name
            if (!newName.matches(nameRegex)) {
                Toast.makeText(this, "Name must be 1-15 letters only", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate password only if user entered one
            if (newPassword.isNotEmpty() && !newPassword.matches(Regex("^\\d{6}$"))) {
                Toast.makeText(this, "Password must be exactly 6 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // All validations passed → update user
            viewModel.updateUser(newName, newEmail, newPassword)
        }

        /**
         * Observe update result from ViewModel
         * Handle different update scenarios (name, email, password)
         * Update UI and navigate accordingly
         */
        viewModel.updateResult.observe(this) { result ->
            when(result){
                // Name updated successfully → return to main screen
                "NAME_CHANGED" -> {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                // Email changed → send verification and force re-login
                "EMAIL_CHANGED" -> {
                    Toast.makeText(this, "Verification email sent. Please login again.", Toast.LENGTH_LONG).show()
                    viewModel.logout()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }

                // Password changed → force re-login
                "PASSWORD_CHANGED" -> {
                    Toast.makeText(this, "Password updated. Please login again.", Toast.LENGTH_LONG).show()
                    viewModel.logout()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                else -> {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        /**
         * Clicking on logout
         * Logout user and navigate to login screen
         */
        logout.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}