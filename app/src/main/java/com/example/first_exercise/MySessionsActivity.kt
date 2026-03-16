package com.example.first_exercise

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.first_exercise.viewmodel.MySessionsViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
class MySessionsActivity : AppCompatActivity() {

    private val vm: MySessionsViewModel by viewModels()

    private lateinit var rv: RecyclerView
    private lateinit var pieChart: PieChart
    private lateinit var emptyText: TextView
    private lateinit var summaryText: TextView
    private lateinit var adapter: SessionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_sessions)

        rv = findViewById(R.id.MySessions)
        pieChart = findViewById(R.id.pieChart)
        emptyText = findViewById(R.id.tvEmpty)
        summaryText = findViewById(R.id.tvCategorySummary)

        rv.layoutManager = LinearLayoutManager(this)

        adapter = SessionAdapter(
            courseId = "",
            onRegClick = { _, _ -> },
            onLocClick = { },
            onSessionClick = { session ->
                val intent = Intent(this, SessionsActivity::class.java).apply {
                    putExtra("COURSE_ID", session.courseId)
                    putExtra("COURSE_NAME", session.courseName)
                }
                startActivity(intent)
            }
        )

        rv.adapter = adapter

        vm.sessions.observe(this) { list ->
            adapter.submitList(list)

            emptyText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        vm.categoryCounts.observe(this) { counts ->
            showDonutChart(counts, pieChart)
            summaryText.text = buildSummaryText(counts)
        }

        vm.loadMySessions()
    }
    override fun onResume() {
        super.onResume()
        vm.loadMySessions()
    }
    private fun showDonutChart(counts: Map<String, Int>, chart: PieChart) {
        val orderedCategories = listOf(
            "Computer Science",
            "Education",
            "Economics",
            "Behavioral Sciences"
        )

        val entries = orderedCategories.map { category ->
            val realValue = counts[category] ?: 0

            // כדי שהצבע תמיד יופיע גם אם הערך הוא 0
            val renderValue = if (realValue == 0) 0.001f else realValue.toFloat()

            PieEntry(renderValue, category, realValue)
        }

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                Color.parseColor("#8BC34A"), // Computer Science
                Color.parseColor("#C8E96A"), // Education
                Color.parseColor("#E9D26A"), // Economics
                Color.parseColor("#7FD3E8")  // Behavioral Sciences
            )
            valueTextSize = 14f
            valueTextColor = Color.BLACK
            sliceSpace = 3f
            yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
            xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
        }

        val data = PieData(dataSet)
//        data.setValueFormatter(object : ValueFormatter() {
//            override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
//                val category = pieEntry?.label ?: ""
//                val realCount = (pieEntry?.data as? Int) ?: 0
//                return "$category\n$realCount"
//            }
//        })
        data.setValueFormatter(object : ValueFormatter() {
            override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
                val category = pieEntry?.label ?: ""
                val total = counts.values.sum()

                if (total == 0) {
                    return "$category\n0%"
                }

                val realCount = (pieEntry?.data as? Int) ?: 0
                val percent = (realCount * 100f) / total

                return "$category\n${percent.toInt()}%"
            }
        })

        chart.data = data
        chart.description.isEnabled = false
        chart.setUsePercentValues(false)
        chart.isDrawHoleEnabled = true
        chart.holeRadius = 55f
        chart.transparentCircleRadius = 60f
        chart.setHoleColor(Color.TRANSPARENT)
        chart.setDrawEntryLabels(false)
        chart.setDrawCenterText(true)
        chart.centerText = "By\nCategory"
        chart.setCenterTextSize(18f)
        chart.rotationAngle = 0f
        chart.isRotationEnabled = true
        chart.animateY(1000)

//        chart.legend.apply {
//            isEnabled = true
//            textSize = 13f
//            form = Legend.LegendForm.CIRCLE
//            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
//            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
//            orientation = Legend.LegendOrientation.HORIZONTAL
//            setDrawInside(false)
//        }
//        chart.legend.apply {
//            isEnabled = true
//            textSize = 10f
//            form = Legend.LegendForm.CIRCLE
//            formSize = 8f
//            xEntrySpace = 8f
//            yEntrySpace = 0f
//            verticalAlignment = Legend.LegendVerticalAlignment.TOP
//            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
//            orientation = Legend.LegendOrientation.HORIZONTAL
//            setDrawInside(false)
//        }
        chart.legend.isEnabled = false
        chart.invalidate()
    }

//    private fun buildSummaryText(counts: Map<String, Int>): String {
//        val computerScience = counts["Computer Science"] ?: 0
//        val education = counts["Education"] ?: 0
//        val economics = counts["Economics"] ?: 0
//        val behavioralSciences = counts["Behavioral Sciences"] ?: 0
//
//        return "Computer Science: $computerScience   |   " +
//                "Education: $education   |   " +
//                "Economics: $economics   |   " +
//                "Behavioral Sciences: $behavioralSciences"
//    }
private fun buildSummaryText(counts: Map<String, Int>): SpannableStringBuilder {
    val computerScience = counts["Computer Science"] ?: 0
    val education = counts["Education"] ?: 0
    val economics = counts["Economics"] ?: 0
    val behavioralSciences = counts["Behavioral Sciences"] ?: 0

    val builder = SpannableStringBuilder()

    appendColoredPrefix(
        builder,
        "● ",
        Color.parseColor("#8BC34A")
    )
    builder.append("Computer Science: $computerScience   |   ")

    appendColoredPrefix(
        builder,
        "● ",
        Color.parseColor("#C8E96A")
    )
    builder.append("Education: $education   |   ")

    appendColoredPrefix(
        builder,
        "● ",
        Color.parseColor("#E9D26A")
    )
    builder.append("Economics: $economics   |   ")

    appendColoredPrefix(
        builder,
        "● ",
        Color.parseColor("#7FD3E8")
    )
    builder.append("Behavioral Sciences: $behavioralSciences")

    return builder
}

    private fun appendColoredPrefix(
        builder: SpannableStringBuilder,
        text: String,
        color: Int
    ) {
        val start = builder.length
        builder.append(text)
        builder.setSpan(
            ForegroundColorSpan(color),
            start,
            builder.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}