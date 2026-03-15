package com.example.first_exercise
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.first_exercise.model.StudySession
import com.example.first_exercise.viewmodel.MySessionsViewModel

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet

import android.view.View
class MySessionsActivity : AppCompatActivity() {

    private val vm: MySessionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_sessions)

        val rv = findViewById<RecyclerView>(R.id.rvMySessions)
        val pieChart = findViewById<PieChart>(R.id.pieChart)
        val emptyText = findViewById<TextView>(R.id.tvEmpty)

        rv.layoutManager = LinearLayoutManager(this)

        vm.sessions.observe(this){ list ->

            if(list.isEmpty()){

                emptyText.visibility =
                    View.VISIBLE
                return@observe

            }

            emptyText.visibility = View.GONE

            showPieChart(list,pieChart)

        }

        vm.loadMySessions()

    }

}
private fun showPieChart(list: List<StudySession>, chart: PieChart){

    val stats = list.groupingBy { it.courseName }.eachCount()

    val entries = stats.map{

        PieEntry(it.value.toFloat(), it.key)

    }

    val dataSet = PieDataSet(entries,"Sessions")

    val data = PieData(dataSet)

    chart.data = data

    chart.invalidate()

}