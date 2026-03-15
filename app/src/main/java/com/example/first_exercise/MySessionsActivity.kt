package com.example.first_exercise
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.first_exercise.model.StudySession
import com.example.first_exercise.viewmodel.MySessionsViewModel
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import android.graphics.Color
import android.content.Intent
import com.github.mikephil.charting.components.Legend

import android.view.View
class MySessionsActivity : AppCompatActivity() {

    private val vm: MySessionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_sessions)

        val rv = findViewById<RecyclerView>(R.id.MySessions)
        val pieChart = findViewById<PieChart>(R.id.pieChart)
        val emptyText = findViewById<TextView>(R.id.tvEmpty)

        rv.layoutManager = LinearLayoutManager(this)

        val adapter = SessionAdapter(
            "",
            onRegClick = { _, _ -> },
            onLocClick = { },
            onSessionClick = { session ->

                val intent = Intent(this, SessionsActivity::class.java)
                intent.putExtra("COURSE_ID", session.courseId)
                intent.putExtra("COURSE_NAME", session.courseName)

                startActivity(intent)
            }
        )

        rv.adapter = adapter

        vm.sessions.observe(this){ list ->

            if(list.isEmpty()){
                emptyText.visibility = View.VISIBLE
                return@observe
            }

            emptyText.visibility = View.GONE

            adapter.submitList(list)   // ← מציג את המפגשים

            showPieChart(list,pieChart) // ← מציג את הפאי
        }

        vm.loadMySessions()

    }

}
private fun showPieChart(list: List<StudySession>, chart: PieChart) {

    val counts = mutableMapOf(
        "Computer Science" to 0,
        "Education" to 0,
        "Economics" to 0,
        "Behavioral Sciences" to 0
    )

    for (session in list) {

        val category = session.courseName

        if (counts.containsKey(category)) {
            counts[category] = counts[category]!! + 1
        }
    }

    val entries = counts.map {
        PieEntry(it.value.toFloat(), it.key)
    }

    val dataSet = PieDataSet(entries, "")

    dataSet.colors = listOf(
        Color.parseColor("#4285F4"),
        Color.parseColor("#34A853"),
        Color.parseColor("#FBBC05"),
        Color.parseColor("#9C27B0")
    )

    dataSet.valueTextSize = 16f
    dataSet.valueTextColor = Color.WHITE

    val data = PieData(dataSet)

    chart.data = data

    chart.description.isEnabled = false
    chart.setUsePercentValues(true)

    chart.isDrawHoleEnabled = false
    chart.setDrawEntryLabels(false)

    chart.legend.isEnabled = true

    chart.animateY(1000)
    chart.invalidate()
}