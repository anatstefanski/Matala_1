package com.example.first_exercise

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.first_exercise.model.CourseItem
import com.example.first_exercise.viewmodel.CourseViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private val viewModel: CourseViewModel by viewModels()
    private lateinit var adapter: CourseAdapter
    private lateinit var progressBar: ProgressBar
    private var isAdmin: Boolean = false
    private var allCourses: List<CourseItem> = emptyList()

    private lateinit var searchInput: EditText
    private lateinit var btnFilter: TextView

    private var currentSearchQuery: String = ""
    private var currentSelectedCategories: Set<String> = setOf("All")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isAdmin = intent.getBooleanExtra("IS_ADMIN", false)

        setupUI()
        observeViewModel()
        viewModel.loadCourses()
    }

    private fun setupUI() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvCourses)
        val fabAddCourse = findViewById<FloatingActionButton>(R.id.fabAdd)
        searchInput = findViewById(R.id.search_input)
        btnFilter = findViewById(R.id.btnFilter)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CourseAdapter { course ->
            val intent = Intent(this, SessionsActivity::class.java)
            intent.putExtra("COURSE_ID", course.courseId)
            intent.putExtra("COURSE_NAME", course.title)
            intent.putExtra("IS_ADMIN", isAdmin)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        fabAddCourse.visibility = if (isAdmin) View.VISIBLE else View.GONE
        fabAddCourse.setOnClickListener { startActivity(Intent(this, AdminActivity::class.java)) }

        setupSearch()
        setupFilter()
    }

    private fun observeViewModel() {
        viewModel.courses.observe(this) { list ->
            allCourses = list
            applySearchAndFilter()
        }
        viewModel.isLoading.observe(this) {
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun applySearchAndFilter() {
        var filtered = allCourses
        if (!currentSelectedCategories.contains("All")) {
            filtered = filtered.filter { it.category in currentSelectedCategories }
        }
        val query = currentSearchQuery.trim()
        if (query.isNotEmpty()) {
            filtered = filtered.filter { it.title.contains(query, ignoreCase = true) }
        }
        adapter.submitList(filtered)
    }

    private fun setupSearch() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s.toString()
                applySearchAndFilter()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFilter() {
        val categories = arrayOf("All", "Computer Science", "Education", "Economics", "Behavioral Sciences")
        val checked = BooleanArray(categories.size)
        checked[0] = true

        btnFilter.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Choose categories")
                .setMultiChoiceItems(categories, checked) { _, which, isChecked ->
                    if (which == 0 && isChecked) {
                        checked.fill(false)
                        checked[0] = true
                        currentSelectedCategories = setOf("All")
                    } else {
                        checked[which] = isChecked
                        currentSelectedCategories = mutableSetOf<String>().apply {
                            for (i in categories.indices) {
                                if (checked[i]) add(categories[i])
                            }
                            if (isEmpty()) add("All")
                            remove("All")
                        }
                    }
                    applySearchAndFilter()
                }
                .setPositiveButton("OK", null)
                .show()
        }
    }
}

class CourseAdapter(private val onCourseClick: (CourseItem) -> Unit) :
    RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    private var courses: List<CourseItem> = emptyList()
    fun submitList(newList: List<CourseItem>) {
        courses = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course_row, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(courses[position], onCourseClick)
    }

    override fun getItemCount() = courses.size

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(course: CourseItem, onClick: (CourseItem) -> Unit) {
            val title = itemView.findViewById<TextView>(R.id.tvCourseTitle)
            val description = itemView.findViewById<TextView>(R.id.tvCourseDescription)
            val image = itemView.findViewById<ImageView>(R.id.ivCourseImage)
            title.text = course.title
            description.text = course.description
            if (!course.imageUrl.isNullOrEmpty()) Picasso.get().load(course.imageUrl).into(image)
            else image.setImageResource(R.drawable.cs1)
            itemView.setOnClickListener { onClick(course) }
        }
    }
}