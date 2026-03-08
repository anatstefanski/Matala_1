package com.example.first_exercise

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.first_exercise.model.CourseItem
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etTitle: EditText = findViewById(R.id.etTitle)
        val etDescription: EditText = findViewById(R.id.etDescription)
        val etImageUrl: EditText = findViewById(R.id.etImageUrl)
        val spinnerCategory: Spinner = findViewById(R.id.spinnerCategory)
        val btnSave: MaterialButton = findViewById(R.id.btnSave)

        val categories = listOf(
            "all",
            "Computer Science",
            "Education",
            "Economics",
            "Behavioral Sciences"
        )
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = spinnerAdapter

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val imageUrl = etImageUrl.text.toString().trim()
            val category = spinnerCategory.selectedItem.toString()

            if (title.isEmpty()) { etTitle.error = "Title required"; return@setOnClickListener }
            if (description.isEmpty()) { etDescription.error = "Description required"; return@setOnClickListener }
            if (imageUrl.isEmpty()) { etImageUrl.error = "Image URL required"; return@setOnClickListener }
            if (spinnerCategory.selectedItemPosition == 0) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val courseId = db.collection("courses").document().id
            val newCourse = CourseItem(courseId, title, description, category, imageUrl)

            db.collection("courses").document(courseId).set(newCourse)
                .addOnSuccessListener {
                    Toast.makeText(this, "Course saved!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving course: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}