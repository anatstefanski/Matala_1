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
    private val userid = "123456789"
    private val username = "User"
    private val password = "123456"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        // Link to login XML
        setContentView(R.layout.activity_login)


        val idInput = findViewById<EditText>(R.id.id_input)
        val usernameInput = findViewById<EditText>(R.id.username_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val loginBtn = findViewById<MaterialButton>(R.id.login_btn)

        //Clicking the LOGIN button
        loginBtn.setOnClickListener {

            //Reading the text the user typed
            val idText = idInput.text.toString().trim()
            val usernameText = usernameInput.text.toString().trim()
            val passwordText = passwordInput.text.toString().trim()

            //Checks that the fields are not empty
            if (idText.isEmpty() || usernameText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //ID must be only digits
            if (!idText.all { it.isDigit() }) {
                Toast.makeText(this, "ID must contain digits only", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //ID must be 9 digits
            if (idText.length != 9) {
                Toast.makeText(this, "ID must contain exactly 9 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            //password must be at least 6 characters long.
            if (passwordText.length < 6) {
                Toast.makeText(this, "Password must contain at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Check if the details are correct
            val isIdCorrect = idText == userid
            val isUsernameCorrect = usernameText == username
            val isPasswordCorrect = passwordText == password

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

