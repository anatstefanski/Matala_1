package com.example.first_exercise

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.first_exercise.R
import com.example.first_exercise.LoginActivity
import com.example.first_exercise.viewmodel.EditProfileViewModel
import com.google.android.material.button.MaterialButton

class EditProfileActivity : AppCompatActivity() {

    private val viewModel: EditProfileViewModel by viewModels()
    private var originalName = ""
    private var originalEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        val username = findViewById<EditText>(R.id.et_username)
        val email = findViewById<EditText>(R.id.et_email)
        val password = findViewById<EditText>(R.id.et_password)
        val saveBtn = findViewById<MaterialButton>(R.id.save_changes_btn)
        val logout = findViewById<TextView>(R.id.logout_text)

        //טעינת הנתונים מהפיירבייס
        viewModel.loadUser()

        viewModel.user.observe(this) { user ->

            username.setText(user.fullName)
            email.setText(user.email)

            originalName = user.fullName
            originalEmail = user.email

        }

        saveBtn.setOnClickListener {

            val newName = username.text.toString().trim()
            val newEmail = email.text.toString().trim()
            val newPassword = password.text.toString()

            // אם לא השתנה כלום
            if (newName == originalName &&
                newEmail == originalEmail &&
                newPassword.isEmpty()) {

                Toast.makeText(this, "No changes were made", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // ===== VALIDATIONS כמו REGISTER =====

            val nameRegex = Regex("^[A-Za-z]{1,5}$")

            if (!newName.matches(nameRegex)) {
                Toast.makeText(this, "Name must be 1-5 letters only", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.isNotEmpty() && !newPassword.matches(Regex("^\\d{6}$"))) {
                Toast.makeText(this, "Password must be exactly 6 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // אם עבר ולידציה → עדכון
            viewModel.updateUser(newName, newEmail, newPassword)
        }

        viewModel.updateResult.observe(this) { result ->

            when(result){

                "NAME_CHANGED" -> {

                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                "EMAIL_CHANGED" -> {

                    Toast.makeText(this, "Verification email sent. Please login again.", Toast.LENGTH_LONG).show()

                    viewModel.logout()

                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }

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
        logout.setOnClickListener {

            viewModel.logout()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()

        }

    }
}