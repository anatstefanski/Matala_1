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
import com.example.first_exercise.model.StudySession
import com.example.first_exercise.viewmodel.SessionViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SessionsActivity : AppCompatActivity() {

    private val vm: SessionViewModel by viewModels()
    private lateinit var adapter: SessionAdapter
    private lateinit var progressBar: ProgressBar
    private var courseId: String = ""
    private var courseName: String = ""
    private var isAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sessions)

        isAdmin = intent.getBooleanExtra("IS_ADMIN", false)
        courseId = intent.getStringExtra("COURSE_ID") ?: ""
        val courseName = intent.getStringExtra("COURSE_NAME") ?: ""

        findViewById<TextView>(R.id.tvCourseHeader).text = courseName
        progressBar = findViewById(R.id.progressBarSessions)

        adapter = SessionAdapter(
            courseId,
            onRegClick = { session, isChecked ->
                vm.toggleRegistration(
                    session,
                    isChecked,
                    courseId
                )
            },
            onLocClick = { session -> openMaps(session.latitude, session.longitude) }
        )
        setupUI()
        vm.sessions.observe(this) {
            adapter.submitList(it)
        }
    }
        private fun setupUI() {
            val rv = findViewById<RecyclerView>(R.id.rvSessions)
            val fabAddSession = findViewById<FloatingActionButton>(R.id.fabAddSession)
            progressBar = findViewById(R.id.progressBarSessions)

            rv.layoutManager = LinearLayoutManager(this)
            rv.adapter = adapter

        fabAddSession.visibility = if (isAdmin) View.VISIBLE else View.GONE
            fabAddSession.setOnClickListener {

                val intent = Intent(this, EditSessionActivity::class.java).apply {
                    putExtra("COURSE_ID", courseId)
                    putExtra("COURSE_NAME", courseName)
                }

                startActivity(intent)
            }
        vm.sessions.observe(this) { adapter.submitList(it) }
        vm.isLoading.observe(this) { progressBar.visibility = if (it) View.VISIBLE else View.GONE }

            if (courseId.isNotEmpty()) {
                vm.loadSessionsForCourse(courseId)
            }
    }

    private fun openMaps(lat: Double, lon: Double) {
        val uri = Uri.parse("google.navigation:q=$lat,$lon")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.google.android.apps.maps") }
        startActivity(intent)
    }
}