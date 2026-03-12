package com.example.first_exercise

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.first_exercise.MainActivity
import com.example.first_exercise.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.viewModels
import com.example.first_exercise.viewmodel.LoginState
import com.example.first_exercise.viewmodel.LoginViewModel
class LoginActivity : AppCompatActivity() {
    private lateinit var onClickListener: () -> Unit
    private val vm: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // זמני לבדיקה – מתנתק כל פעם שהמסך נפתח
        FirebaseAuth.getInstance().signOut()
        // Link to login XML
        setContentView(R.layout.activity_login)

        // 1) If already logged in -> go directly to MainActivity
        if (vm.isLoggedIn()) {
            vm.checkAdminForExistingUser { isAdmin ->
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("IS_ADMIN", isAdmin)
                startActivity(intent)
                finish()
            }
            return
        }

        // 2) Observe login result from ViewModel
        vm.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Loading -> {
                    // Optional: disable button / show progress
                }
                is LoginState.Success -> {
                    Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("IS_ADMIN", state.isAdmin) // מעבירים את הנתון הלאה
                    startActivity(intent)
                    finish()
                }
                is LoginState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 3) Get Views (IDs match your XML: email_input, password_input)
        val emailInput = findViewById<EditText>(R.id.email_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val loginBtn = findViewById<MaterialButton>(R.id.login_btn)
        val forgotPasswordText = findViewById<TextView>(R.id.forgot_password_text)

        forgotPasswordText.setOnClickListener {

            val email = emailInput.text.toString().trim()

            // בדיקה אם ריק
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // בדיקה אם המייל תקין
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // שליחת מייל לשינוי סיסמה
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Password reset email sent",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Failed to send reset email",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        // 4) Click handler
        loginBtn.setOnClickListener {
            val emailText = emailInput.text.toString().trim()
            val passwordText = passwordInput.text.toString().trim()

            // empty
            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Email and password must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

// email format (basic)
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


// password: EXACTLY 6 digits
            if (passwordText.length != 6 || !passwordText.all { it.isDigit() }) {
                Toast.makeText(this, "Password must be exactly 6 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Call ViewModel (which calls Repository -> Firebase)
            vm.login(emailText, passwordText)
            }

        val goToRegister = findViewById<TextView>(R.id.go_to_register)

        goToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        }


    }



