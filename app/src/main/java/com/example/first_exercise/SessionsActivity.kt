package com.example.first_exercise

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.first_exercise.viewmodel.SessionViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.Toast

/**
 * Displays all sessions of a selected course.
 * Allows students to register and admins to create sessions.
 */
class SessionsActivity : AppCompatActivity() {

    private val vm: SessionViewModel by viewModels()
    private lateinit var adapter: SessionAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyText: TextView
    private var courseId: String = ""
    private var courseName: String = ""
    private var isAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sessions)

        isAdmin = intent.getBooleanExtra("IS_ADMIN", false)
        courseId = intent.getStringExtra("COURSE_ID") ?: ""
        if (courseId.isEmpty()) {
            Toast.makeText(this, "Error loading course", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        courseName = intent.getStringExtra("COURSE_NAME") ?: ""

        val tvHeader = findViewById<TextView>(R.id.tvCourseHeader)
        tvHeader.text = courseName
        progressBar = findViewById(R.id.progressBarSessions)
        emptyText = findViewById(R.id.tvEmptySessions)

        adapter = SessionAdapter(
            courseId,
            isAdmin,
            // Handles register/unregister click from user
            onRegClick = { session, isChecked ->
                vm.toggleRegistration(session, isChecked, courseId, isAdmin)
            },
            // Opens navigation if location exists
            onLocClick = { session ->
                if (session.latitude == 0.0 && session.longitude == 0.0) {
                    Toast.makeText(this, "No location available", Toast.LENGTH_SHORT).show()
                } else {
                    openMaps(session.latitude, session.longitude)
                }
            },
            onSessionClick = { }
        )

        setupUI()

        vm.sessions.observe(this) { list ->

            adapter.submitList(list)

            //no sessions
            if (list.isEmpty()) {
                emptyText.visibility = View.VISIBLE
            } else {
                emptyText.visibility = View.GONE
            }
        }

        vm.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Setup RecyclerView with adapter
     */
    private fun setupUI() {

        val rv = findViewById<RecyclerView>(R.id.rvSessions)
        val fabAddSession = findViewById<FloatingActionButton>(R.id.fabAddSession)

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        fabAddSession.visibility = if (isAdmin) View.VISIBLE else View.GONE

        // Navigate to create new session screen
        fabAddSession.setOnClickListener {

            val intent = Intent(this, EditSessionActivity::class.java).apply {
                putExtra("COURSE_ID", courseId)
                putExtra("COURSE_NAME", courseName)
            }

            startActivity(intent)
        }

        vm.isLoading.observe(this) {
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        if (courseId.isNotEmpty()) {
            vm.loadSessionsForCourse(courseId)
        }
    }

    // Opens Google Maps navigation if available
    private fun openMaps(lat: Double, lon: Double) {
        val uri = Uri.parse("google.navigation:q=$lat,$lon")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No maps app found", Toast.LENGTH_SHORT).show()
        }
    }
}