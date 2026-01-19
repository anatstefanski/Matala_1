package com.example.first_exercise

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.first_exercise.MainActivity
import com.example.first_exercise.R
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {

    //Local data
    private val ID = "123456789"
    private val Username = "User"
    private val Password = "123456"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        // Link to login XML
        setContentView(R.layout.activity_login)

        // ⛔ זמני לפיתוח – למחוק לפני הגשה
        if (true) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val IDInput = findViewById<EditText>(R.id.id_input)
        val UsernameInput = findViewById<EditText>(R.id.username_input)
        val PasswordInput = findViewById<EditText>(R.id.password_input)
        val loginBtn = findViewById<MaterialButton>(R.id.login_btn)

        //Clicking the LOGIN button
        loginBtn.setOnClickListener {

            //Reading the text the user typed
            val IDText = IDInput.text.toString().trim()
            val UsernameText = UsernameInput.text.toString().trim()
            val PasswordText = PasswordInput.text.toString().trim()

            //Checks that the fields are not empty
            if (IDText.isEmpty() || UsernameText.isEmpty() || PasswordText.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //ID must be only digits
            if (!IDText.all { it.isDigit() }) {
                Toast.makeText(this, "ID must contain digits only", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //ID must be 9 digits
            if (IDText.length != 9) {
                Toast.makeText(this, "ID must contain exactly 9 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            //password must be at least 6 characters long.
            if (PasswordText.length < 6) {
                Toast.makeText(this, "Password must contain at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Check if the details are correct
            val isIdCorrect = IDText == ID
            val isUsernameCorrect = UsernameText == Username
            val isPasswordCorrect = PasswordText == Password

            if (isIdCorrect && isUsernameCorrect && isPasswordCorrect) {
                // if all the details are correct Go to the main screen

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                // If any of the details are incorrect, an error message is returned
                Toast.makeText(
                    this,
                    "Incorrect ID, username or password",
                    Toast.LENGTH_SHORT

                ).show()
            }
        }
    }
}

