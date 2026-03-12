package com.example.first_exercise

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.first_exercise.R
import com.example.first_exercise.viewmodel.AdminViewModel
import com.google.android.material.button.MaterialButton
import android.widget.Toast
import com.example.first_exercise.model.CourseItem

class AdminActivity : AppCompatActivity() {

    private val vm: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

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

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        btnSave.setOnClickListener {

            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val imageUrl = etImageUrl.text.toString().trim()
            val category = spinnerCategory.selectedItem.toString()

            if (title.isEmpty()) {
                etTitle.error = "Title required"
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                etDescription.error = "Description required"
                return@setOnClickListener
            }

            if (imageUrl.isEmpty()) {
                etImageUrl.error = "Image URL required"
                return@setOnClickListener
            }

            if (spinnerCategory.selectedItemPosition == 0) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val course = CourseItem(
                courseId = "",
                title = title,
                description = description,
                category = category,
                imageUrl = imageUrl
            )

            vm.saveCourse(course) { success ->
                if (success) {
                    Toast.makeText(this, "Course saved!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error saving course", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
