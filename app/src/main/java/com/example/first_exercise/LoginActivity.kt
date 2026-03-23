package com.example.first_exercise

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import androidx.activity.viewModels
import com.example.first_exercise.viewmodel.LoginState
import com.example.first_exercise.viewmodel.LoginViewModel
class LoginActivity : AppCompatActivity() {
    private val vm: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Link to login XML
        setContentView(R.layout.activity_login)

        val emailInput = findViewById<EditText>(R.id.email_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val loginBtn = findViewById<MaterialButton>(R.id.login_btn)
        val forgotPasswordText = findViewById<TextView>(R.id.forgot_password_text)
        val goToRegister = findViewById<TextView>(R.id.go_to_register)

        /**
        * If already logged in-go directly to MainActivity
        */
        if (vm.isLoggedIn()) {
            vm.checkAdminForExistingUser { isAdmin ->
                val userId = vm.getCurrentUserId() ?: ""
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("IS_ADMIN", isAdmin)
                    putExtra("USER_ID", userId)
                }
                startActivity(intent)
                finish()
            }
            return
        }

        /**
         *Observe login result from ViewModel
         *Disable button during loading to prevent multiple clicks
        */
        vm.loginState.observe(this) { state ->
            when (state) {
                //Prevents double clicking
                is LoginState.Loading -> {
                    loginBtn.isEnabled = false
                    loginBtn.alpha = 0.5f
                }
                //Successfully logged in
                is LoginState.Success -> {
                    loginBtn.isEnabled = true
                    loginBtn.alpha = 1f
                    Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show()
                    val userId = vm.getCurrentUserId() ?: ""
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("IS_ADMIN", state.isAdmin)
                        putExtra("USER_ID", userId)
                    }
                    startActivity(intent)
                    finish()
                }
                is LoginState.Error -> {
                    loginBtn.isEnabled = true
                    loginBtn.alpha = 1f
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        /**
        *If you forget your password, you can recover it by sending an email to reset your password.
         */
        forgotPasswordText.setOnClickListener {

            val email = emailInput.text.toString().trim()

            // Email field must be filled in.
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the email is valid
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Send an email to change your password
            vm.resetPassword(email) { success, error ->
                if (success) {
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, error ?: "Failed to send reset email", Toast.LENGTH_LONG).show()
                }
            }
        }

        //Click on login button
        loginBtn.setOnClickListener {
            val emailText = emailInput.text.toString().trim()
            val passwordText = passwordInput.text.toString().trim()

            // empty
            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Email and password must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // password: EXACTLY 6 digits
            if (passwordText.length != 6 || !passwordText.all { it.isDigit() }) {
                Toast.makeText(this, "Password must be exactly 6 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            vm.login(emailText, passwordText)
            }


        /**
        *If not registered in the app – clicking "Register"
        navigates to the registration screen.
         */
        goToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        }


    }



