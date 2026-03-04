package com.example.first_exercise

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
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
        setContentView(R.layout.activity_register)

        val fullNameInput = findViewById<EditText>(R.id.fullname_input)
        val emailInput = findViewById<EditText>(R.id.email_input)
        val passInput = findViewById<EditText>(R.id.password_input)
        val confirmInput = findViewById<EditText>(R.id.confirm_password_input)

        val registerBtn = findViewById<MaterialButton>(R.id.register_btn)


        vm.registerState.observe(this) { state ->
            when (state) {

                is RegisterState.Loading -> {
                    registerBtn.isEnabled = false
                }

                is RegisterState.Success -> {
                    registerBtn.isEnabled = true

                    Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }

                is RegisterState.Error -> {
                    registerBtn.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        registerBtn.setOnClickListener {
            val fullName = fullNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val pass = passInput.text.toString()
            val confirm = confirmInput.text.toString()

            // ולידציות ב-Controller (לפי הדרישה שה-View הוא Activity/Fragment)
            if (fullName.length < 2) {
                toast("Please enter full name")
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                toast("Invalid email")
                return@setOnClickListener
            }
            if (!pass.matches(Regex("^\\d{6}$"))) {
                toast("Password must be exactly 6 digits")
                return@setOnClickListener
            }
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