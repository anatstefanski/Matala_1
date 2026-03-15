package com.example.first_exercise

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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.first_exercise.model.CourseItem
import com.example.first_exercise.viewmodel.CourseViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private val viewModel: CourseViewModel by viewModels()

    private lateinit var adapter: CourseAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var loadMoreButton: View
    private var isAdmin: Boolean = false

    private lateinit var searchInput: EditText
    private lateinit var btnFilter: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isAdmin = intent.getBooleanExtra("IS_ADMIN", false)

        setupUI()
        observeViewModel()

        // טעינה ראשונית
        viewModel.loadFirstCourses()

        val profileBtn = findViewById<FloatingActionButton>(R.id.fabProfile)

        profileBtn.setOnClickListener {

            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)

        }
    }

    private fun setupUI() {

        val recyclerView = findViewById<RecyclerView>(R.id.rvCourses)
        val fabAddCourse = findViewById<FloatingActionButton>(R.id.fabAdd)


        searchInput = findViewById(R.id.search_input)
        btnFilter = findViewById(R.id.btnFilter)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CourseAdapter { course ->

            val intent = Intent(this, SessionsActivity::class.java).apply {
                putExtra("COURSE_ID", course.courseId)
                putExtra("COURSE_NAME", course.title)
                putExtra("IS_ADMIN", isAdmin)
            }

            startActivity(intent)

        }

        recyclerView.adapter = adapter

        fabAddCourse.visibility = if (isAdmin) View.VISIBLE else View.GONE

        fabAddCourse.setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
        }
        loadMoreButton = findViewById(R.id.btnLoadMore)

        loadMoreButton.setOnClickListener {
            viewModel.loadMoreCourses()
        }


        setupSearch()
        setupFilter()
    }

    private fun observeViewModel() {

        viewModel.filteredCourses.observe(this) { list ->

            adapter.submitList(list)

        }

        viewModel.isLoading.observe(this) { loading ->

            progressBar.visibility =
                if (loading) View.VISIBLE
                else View.GONE

        }

    }

    private fun setupSearch() {

        searchInput.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                viewModel.updateSearchAndFilter(
                    query = s.toString()
                )

            }
        })
    }

    private fun setupFilter() {

        btnFilter.setOnClickListener {

            val categories = arrayOf(
                "All",
                "Computer Science",
                "Education",
                "Economics",
                "Behavioral Sciences"
            )

            val selectedCategories = mutableSetOf<String>()

            val checkedItems = BooleanArray(categories.size) { false }

            val builder = AlertDialog.Builder(this)

            builder.setTitle("Select Categories")

            builder.setMultiChoiceItems(categories, checkedItems) { _, which, isChecked ->

                if (isChecked)
                    selectedCategories.add(categories[which])
                else
                    selectedCategories.remove(categories[which])

            }

            builder.setPositiveButton("Apply") { _, _ ->

                if (selectedCategories.isEmpty())
                    selectedCategories.add("All")

                viewModel.updateSearchAndFilter(
                    categories = selectedCategories
                )

            }

            builder.setNegativeButton("Cancel", null)

            builder.show()
        }
    }

    // Adapter
    class CourseAdapter(
        private val onCourseClick: (CourseItem) -> Unit
    ) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

        private var courses: List<CourseItem> = emptyList()

        fun submitList(newList: List<CourseItem>) {

            courses = newList
            notifyDataSetChanged()

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {

            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_course_row, parent, false)

            return CourseViewHolder(view)

        }

        override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {

            holder.bind(courses[position], onCourseClick)

        }

        override fun getItemCount() = courses.size

        class CourseViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {

            fun bind(course: CourseItem, onClick: (CourseItem) -> Unit) {

                val title = itemView.findViewById<TextView>(R.id.tvCourseTitle)
                val description = itemView.findViewById<TextView>(R.id.tvCourseDescription)
                val image = itemView.findViewById<ImageView>(R.id.ivCourseImage)

                title.text = course.title
                description.text = course.description

                if (!course.imageUrl.isNullOrEmpty())
                    Picasso.get().load(course.imageUrl).into(image)
                else
                    image.setImageResource(R.drawable.cs1)

                itemView.setOnClickListener {

                    onClick(course)

                }
            }
        }
    }
}