package com.example.first_exercise

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.first_exercise.model.StudySession
import com.example.first_exercise.viewmodel.SessionEditViewModel
import com.google.android.material.button.MaterialButton

class EditSessionActivity : AppCompatActivity() {

    private val vm: SessionEditViewModel by viewModels()

    private var courseId: String? = null
    private var courseName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_session)

        val etTopic: EditText = findViewById(R.id.etTopic)
        val etDate: EditText = findViewById(R.id.etDate)
        val etTime: EditText = findViewById(R.id.etTime)
        val etLat: EditText = findViewById(R.id.etLat)
        val etLon: EditText = findViewById(R.id.etLon)
        val etZoomUrl: EditText = findViewById(R.id.etZoomUrl)
        val btnSave: MaterialButton = findViewById(R.id.btnSaveSession)

        courseId = intent.getStringExtra("COURSE_ID")
        courseName = intent.getStringExtra("COURSE_NAME")

        btnSave.setOnClickListener {

            val topic = etTopic.text.toString().trim()
            val date = etDate.text.toString().trim()
            val time = etTime.text.toString().trim()
            val lat = etLat.text.toString().toDoubleOrNull() ?: 0.0
            val lon = etLon.text.toString().toDoubleOrNull() ?: 0.0
            val zoomUrl = etZoomUrl.text.toString().trim().ifEmpty { null }

            if (topic.isEmpty()) {
                etTopic.error = "Topic required"
                return@setOnClickListener
            }

            if (date.isEmpty()) {
                etDate.error = "Date required"
                return@setOnClickListener
            }

            if (time.isEmpty()) {
                etTime.error = "Time required"
                return@setOnClickListener
            }

            val session = StudySession(
                sessionId = "",
                courseId = courseId ?: "",
                courseName = courseName ?: "",
                topic = topic,
                date = date,
                time = time,
                latitude = lat,
                longitude = lon,
                zoomUrl = zoomUrl,
                participantsCount = 0
            )

            vm.saveSession(session) { success ->

                if (success) {
                    Toast.makeText(this, "Session saved!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error saving session", Toast.LENGTH_LONG).show()
                }

            }
        }
    }
}
