package com.example.first_exercise

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔹 Enable edge-to-edge display
        enableEdgeToEdge()

        setContentView(R.layout.activity_admin)

        // 🔹 Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔹 Views
        val etTitle: EditText = findViewById(R.id.etTitle)
        val etDescription: EditText = findViewById(R.id.etDescription)
        val etImageUrl: EditText = findViewById(R.id.etImageUrl)
        val etVideoUrl: EditText = findViewById(R.id.etVideoUrl)
        val spinnerCategory: Spinner = findViewById(R.id.spinnerCategory)
        val btnSave: Button = findViewById(R.id.btnSave)

        // 🔹 Spinner data
        val categories = listOf(
            "all",
            "Computer Science",
            "Education",
            "Economics",
            "Behavioral Sciences"
        )

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = spinnerAdapter

        // 🔹 Save button logic
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val imageUrl = etImageUrl.text.toString().trim()
            val videoUrl = etVideoUrl.text.toString().trim()
            val category = spinnerCategory.selectedItem.toString()

            // 🔹 Validation
            if (title.isEmpty()) {
                etTitle.error = "Title is required"
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                etDescription.error = "Description is required"
                return@setOnClickListener
            }

            if (imageUrl.isEmpty()) {
                etImageUrl.error = "Image URL is required"
                return@setOnClickListener
            }

            if (videoUrl.isEmpty()) {
                etVideoUrl.error = "Video URL is required"
                return@setOnClickListener
            }

            if (spinnerCategory.selectedItemPosition == 0) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 Create CourseItem
            val newCourse = CourseItem(
                title = title,
                description = description,
                category = category,
                imageRes = R.drawable.cs1, // placeholder
                videoUrl = videoUrl,
                imageUrl = imageUrl
            )

            // 🔹 Return result to MainActivity
            val resultIntent = Intent()
            resultIntent.putExtra("new_item", newCourse)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
