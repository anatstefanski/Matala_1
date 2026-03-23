package com.example.first_exercise

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.first_exercise.viewmodel.RegisterState
import com.example.first_exercise.viewmodel.RegisterViewModel
import com.google.android.material.button.MaterialButton

class RegisterActivity : AppCompatActivity() {

    private val vm: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Link to XML
        setContentView(R.layout.activity_register)

        val fullNameInput = findViewById<EditText>(R.id.fullname_input)
        val emailInput = findViewById<EditText>(R.id.email_input)
        val passInput = findViewById<EditText>(R.id.password_input)
        val confirmInput = findViewById<EditText>(R.id.confirm_password_input)
        val registerBtn = findViewById<MaterialButton>(R.id.register_btn)

        /**
         * Observe register state from ViewModel
         * Disable button during loading to prevent multiple clicks
         * Show success or error message to the user
         */
        vm.registerState.observe(this) { state ->
            when (state) {
                //Prevents double clicking
                is RegisterState.Loading -> {
                    registerBtn.isEnabled = false
                    registerBtn.alpha = 0.5f
                }
                //Successfully registered
                is RegisterState.Success -> {
                    registerBtn.isEnabled = true
                    registerBtn.alpha = 1f
                    Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                is RegisterState.Error -> {
                    registerBtn.isEnabled = true
                    registerBtn.alpha = 1f
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        /**
         * Handles register button click.
         *
         * Retrieves user input, validates it, and triggers registration via ViewModel.
         */
        registerBtn.setOnClickListener {
            val fullName = fullNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val pass = passInput.text.toString()
            val confirm = confirmInput.text.toString()
            // Regex pattern: allows only letters (A-Z, a-z), length between 1 and 15
            val nameRegex = Regex("^[A-Za-z]{1,15}$")

            if (!fullName.matches(nameRegex)) {
                toast("Full name must be 1–15 letters only")
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                toast("Invalid email")
                return@setOnClickListener
            }
            // Validate password: must be exactly 6 digits
            if (!pass.matches(Regex("^\\d{6}$"))) {
                toast("Password must be exactly 6 digits")
                return@setOnClickListener
            }
            // Check if password and confirmation match
            if (pass != confirm) {
                toast("Passwords do not match")
                return@setOnClickListener
            }

            vm.register(fullName, email, pass)
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}