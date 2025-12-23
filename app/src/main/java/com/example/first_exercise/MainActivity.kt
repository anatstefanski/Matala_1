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
import android.view.MotionEvent
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.net.Uri




class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val recyclerView: RecyclerView = findViewById(R.id.rvCourses)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CatalogAdapter(courses)
        recyclerView.adapter = adapter
        val allCourses = courses.toList()

        //Search function
        fun applySearch(query: String) {
            val q = query.trim()

            val filtered = if (q.isEmpty()) {
                allCourses
            } else {
                allCourses.filter { it.title.contains(q, ignoreCase = true) }
            }

            adapter.updateItems(filtered)
        }

        //Filter function
        fun applyCategoryMultiFilter(selected: Set<String>) {
            val filtered = if (selected.isEmpty() || selected.contains("All")) {
                allCourses
            } else {
                allCourses.filter { it.category in selected }
            }
            adapter.updateItems(filtered)
        }


        val btnFilter: TextView = findViewById(R.id.btnFilter)

        val categories = arrayOf(
            "All",
            "Computer Science",
            "Education",
            "Economics",
            "Behavioral Sciences"
        )

        val checked = BooleanArray(categories.size)
        checked[0] = true  //All

        val selectedCategories = mutableSetOf("All")

        btnFilter.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Choose categories")
                .setMultiChoiceItems(categories, checked) { dialogInterface, which, isChecked ->

                    val dialog = dialogInterface as androidx.appcompat.app.AlertDialog
                    val listView = dialog.listView

                    checked[which] = isChecked

                    if (which == 0 && isChecked) {
                        // If you select All,cancels everything else
                        selectedCategories.clear()
                        selectedCategories.add("All")
                        for (i in 1 until checked.size) {
                            checked[i] = false
                            listView.setItemChecked(i, false)
                        }
                    } else {
                        // Select/Remove a standard category
                        if (isChecked) selectedCategories.add(categories[which])
                        else selectedCategories.remove(categories[which])

                        //If you select something ALL is canceled.
                        if (selectedCategories.isNotEmpty()) {
                            selectedCategories.remove("All")
                            checked[0] = false
                            listView.setItemChecked(0, false)
                        }

                        //If you deselect everything, it returns to ALL.
                        if (selectedCategories.isEmpty()) {
                            selectedCategories.add("All")
                            checked[0] = true
                            listView.setItemChecked(0, true)
                        }
                    }

                    // Immediate update
                    applyCategoryMultiFilter(selectedCategories)
                }
                .setNegativeButton("OK", null)

            val dialog = builder.create()
            dialog.show()
        }




        //Clicking on the search icon
        val searchInput: EditText = findViewById(R.id.search_input)
        searchInput.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {


                val startDrawable = searchInput.compoundDrawables[0]
                val endDrawable = searchInput.compoundDrawables[2]

                // Click on START icon (usually left)
                if (startDrawable != null) {
                    val iconWidth = startDrawable.bounds.width()
                    val touchAreaEnd = searchInput.paddingStart + iconWidth
                    if (event.x <= touchAreaEnd) {
                        applySearch(searchInput.text?.toString().orEmpty())
                        return@setOnTouchListener true
                    }
                }

               // Click on END icon (usually right)
                if (endDrawable != null) {
                    val iconWidth = endDrawable.bounds.width()
                    val touchAreaStart = searchInput.width - searchInput.paddingEnd - iconWidth
                    if (event.x >= touchAreaStart) {
                        applySearch(searchInput.text?.toString().orEmpty())

                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }

    val courses = arrayListOf(
        // Computer Science
        CourseItem("Introduction to Computer Science", "Fundamentals of programming and algorithmic thinking", "Computer Science", R.drawable.cs1, "https://www.youtube.com/watch?v=pjb614oik9U"),
        CourseItem("Object-Oriented Programming", "OOP principles and software development", "Computer Science", R.drawable.cs2, "https://www.youtube.com/watch?v=pjb614oik9U"),
        CourseItem("Data Structures", "Efficient data organization techniques", "Computer Science", R.drawable.cs3, "https://www.youtube.com/watch?v=pjb614oik9U"),
        CourseItem("Algorithms", "Problem solving and algorithm design", "Computer Science", R.drawable.cs4, "https://www.youtube.com/watch?v=pjb614oik9U"),
        CourseItem("Operating Systems", "Process, memory, and resource management", "Computer Science", R.drawable.cs5, "https://www.youtube.com/watch?v=pjb614oik9U"),

        // Education
        CourseItem("Introduction to Education", "Foundations of educational theory", "Education", R.drawable.edu1, "https://www.youtube.com/watch?v=Np0XCwVu7po"),
        CourseItem("Educational Psychology", "Learning and development processes", "Education", R.drawable.edu2, "https://www.youtube.com/watch?v=Np0XCwVu7po"),
        CourseItem("Teaching and Learning Methods", "Pedagogical strategies", "Education", R.drawable.edu3, "https://www.youtube.com/watch?v=Np0XCwVu7po"),
        CourseItem("Educational Assessment and Evaluation", "Measurement and evaluation tools", "Education", R.drawable.edu4, "https://www.youtube.com/watch?v=Np0XCwVu7po"),
        CourseItem("Learning Technologies", "Technology integration in education", "Education", R.drawable.edu5, "https://www.youtube.com/watch?v=Np0XCwVu7po"),

        // Economics
        CourseItem("Introduction to Microeconomics", "Consumer and producer behavior", "Economics", R.drawable.eco1, "https://www.youtube.com/watch?v=egVFmxrOxyk"),
        CourseItem("Introduction to Macroeconomics", "Inflation, unemployment, and growth", "Economics", R.drawable.eco2, "https://www.youtube.com/watch?v=egVFmxrOxyk"),
        CourseItem("Game Theory", "Strategic decision-making models", "Economics", R.drawable.eco3, "https://www.youtube.com/watch?v=egVFmxrOxyk"),
        CourseItem("Public Economics", "Taxation and public expenditure", "Economics", R.drawable.eco4, "https://www.youtube.com/watch?v=egVFmxrOxyk"),
        CourseItem("Financial Markets", "Capital markets and investments", "Economics", R.drawable.eco5, "https://www.youtube.com/watch?v=egVFmxrOxyk"),

        // Behavioral Sciences
        CourseItem("Introduction to Behavioral Sciences", "Human behavior and social systems", "Behavioral Sciences", R.drawable.beh1, "https://www.youtube.com/watch?v=B07wJ9ZUOHA"),
        CourseItem("Social Psychology", "Social influence on individuals", "Behavioral Sciences", R.drawable.beh2, "https://www.youtube.com/watch?v=B07wJ9ZUOHA"),
        CourseItem("Organizational Behavior", "Behavior in organizational settings", "Behavioral Sciences", R.drawable.beh3, "https://www.youtube.com/watch?v=B07wJ9ZUOHA"),
        CourseItem("Decision Making", "Cognitive and behavioral decision processes", "Behavioral Sciences", R.drawable.beh4, "https://www.youtube.com/watch?v=B07wJ9ZUOHA"),
        CourseItem("Cognitive Psychology", "Perception, memory, and thinking", "Behavioral Sciences", R.drawable.beh5, "https://www.youtube.com/watch?v=B07wJ9ZUOHA")
    )
    class CatalogAdapter(
        private var items: List<CourseItem>
    ) : RecyclerView.Adapter<CatalogAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTv: TextView = itemView.findViewById(R.id.tvCourseTitle)
            val desTv:TextView=itemView.findViewById(R.id.tvCourseDescription)
            val imageIv: ImageView = itemView.findViewById(R.id.ivCourseImage)

            val playBtn: View = itemView.findViewById(R.id.btnPlay)

        }

        // Number of items in the list
        override fun getItemCount(): Int = items.size

        // Create a new ViewHolder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_course_row, parent, false)
            return ViewHolder(view)
        }

        // Connect the data to the ViewHolder
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.titleTv.text = item.title
            holder.imageIv.setImageResource(item.imageRes)
            holder.desTv.text=item.description
            holder.playBtn.setOnClickListener {
                val url = item.videoUrl  // או item.videoLink / item.url לפי השם אצלך
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(Intent.createChooser(intent, "Open video with"))
            }

        }
            // Function to update list
            fun updateItems(newItems: List<CourseItem>) {
            items = newItems
            notifyDataSetChanged()
        }
    }



}
