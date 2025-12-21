package com.example.first_exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val recyclerView: RecyclerView = findViewById(R.id.rvCourses)
        recyclerView.layoutManager = LinearLayoutManager(this) // קובע את הצגת הרשימה כקו אנכי
        val adapter = CatalogAdapter(courses)
        recyclerView.adapter = adapter
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    val courses = arrayListOf(
        // Computer Science
        CourseItem("Introduction to Computer Science", "Fundamentals of programming and algorithmic thinking", "Computer Science", R.drawable.cs1, "https://youtu.be/..."),
        CourseItem("Object-Oriented Programming", "OOP principles and software development", "Computer Science", R.drawable.cs2, "https://youtu.be/..."),
        CourseItem("Data Structures", "Efficient data organization techniques", "Computer Science", R.drawable.cs3, "https://youtu.be/..."),
        CourseItem("Algorithms", "Problem solving and algorithm design", "Computer Science", R.drawable.cs4, "https://youtu.be/..."),
        CourseItem("Operating Systems", "Process, memory, and resource management", "Computer Science", R.drawable.cs5, "https://youtu.be/..."),

        // Education
        CourseItem("Introduction to Education", "Foundations of educational theory", "Education", R.drawable.edu1, "https://youtu.be/..."),
        CourseItem("Educational Psychology", "Learning and development processes", "Education", R.drawable.edu2, "https://youtu.be/..."),
        CourseItem("Teaching and Learning Methods", "Pedagogical strategies", "Education", R.drawable.edu3, "https://youtu.be/..."),
        CourseItem("Educational Assessment and Evaluation", "Measurement and evaluation tools", "Education", R.drawable.edu4, "https://youtu.be/..."),
        CourseItem("Learning Technologies", "Technology integration in education", "Education", R.drawable.edu5, "https://youtu.be/..."),

        // Economics
        CourseItem("Introduction to Microeconomics", "Consumer and producer behavior", "Economics", R.drawable.eco1, "https://youtu.be/..."),
        CourseItem("Introduction to Macroeconomics", "Inflation, unemployment, and growth", "Economics", R.drawable.eco2, "https://youtu.be/..."),
        CourseItem("Game Theory", "Strategic decision-making models", "Economics", R.drawable.eco3, "https://youtu.be/..."),
        CourseItem("Public Economics", "Taxation and public expenditure", "Economics", R.drawable.eco4, "https://youtu.be/..."),
        CourseItem("Financial Markets", "Capital markets and investments", "Economics", R.drawable.eco5, "https://youtu.be/..."),

        // Behavioral Sciences
        CourseItem("Introduction to Behavioral Sciences", "Human behavior and social systems", "Behavioral Sciences", R.drawable.beh1, "https://youtu.be/..."),
        CourseItem("Social Psychology", "Social influence on individuals", "Behavioral Sciences", R.drawable.beh2, "https://youtu.be/..."),
        CourseItem("Organizational Behavior", "Behavior in organizational settings", "Behavioral Sciences", R.drawable.beh3, "https://youtu.be/..."),
        CourseItem("Decision Making", "Cognitive and behavioral decision processes", "Behavioral Sciences", R.drawable.beh4, "https://youtu.be/..."),
        CourseItem("Cognitive Psychology", "Perception, memory, and thinking", "Behavioral Sciences", R.drawable.beh5, "https://youtu.be/...")
    )
    class CatalogAdapter(
        private var items: List<CourseItem>
    ) : RecyclerView.Adapter<CatalogAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTv: TextView = itemView.findViewById(R.id.tvCourseTitle)
            val desTv:TextView=itemView.findViewById(R.id.tvCourseDescription)
            val imageIv: ImageView = itemView.findViewById(R.id.ivCourseImage)
        }

        // מספר הפריטים ברשימה
        override fun getItemCount(): Int = items.size

        // יצירת ViewHolder חדש
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_course_row, parent, false) // הכנס את layout של הפריט שלך
            return ViewHolder(view)
        }

        // חיבור הנתונים ל-ViewHolder
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.titleTv.text = item.title
            holder.imageIv.setImageResource(item.imageRes)
            holder.desTv.text=item.description
        }

        // פונקציה לעדכון רשימה
        fun updateItems(newItems: List<CourseItem>) {
            items = newItems
            notifyDataSetChanged()
        }
    }


    }