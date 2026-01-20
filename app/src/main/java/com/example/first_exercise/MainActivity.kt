package com.example.first_exercise

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    // Displayed list defaults + admin added
    private val courses = arrayListOf<CourseItem>()

    // Only admin-added items (saved in SharedPreferences)
    private val adminAdded = arrayListOf<CourseItem>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CatalogAdapter
    private var allCourses: List<CourseItem> = emptyList()
    private lateinit var addItemLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Load ONLY admin-added items
        adminAdded.clear()
        adminAdded.addAll(StorageManager.loadAddedItems(this))

        // Build display list = default + adminAdded
        rebuildCoursesList()
        recyclerView = findViewById(R.id.rvCourses)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CatalogAdapter(courses)
        recyclerView.adapter = adapter

        attachSwipeToDelete()

        // Getting a new item from the AdminActivity
        addItemLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val newItem = data?.getSerializableExtra("new_item") as? CourseItem
                if (newItem != null) {
                    adminAdded.add(newItem)
                    StorageManager.saveAddedItems(this, adminAdded)
                    rebuildCoursesList()
                    adapter.updateItems(allCourses)
                }
            }
        }

        // Clicking the add button
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        fabAdd.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            addItemLauncher.launch(intent)
        }

        // Search
        fun applySearch(query: String) {
            val q = query.trim()
            val filtered = if (q.isEmpty()) {
                allCourses
            } else {
                allCourses.filter { it.title.contains(q, ignoreCase = true) }
            }
            adapter.updateItems(filtered)
        }

        // Filter
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
        checked[0] = true

        val selectedCategories = mutableSetOf("All")

        btnFilter.setOnClickListener {
            val builder = AlertDialog.Builder(this)
                .setTitle("Choose categories")
                .setMultiChoiceItems(categories, checked) { dialogInterface, which, isChecked ->

                    val dialog = dialogInterface as AlertDialog
                    val listView = dialog.listView

                    checked[which] = isChecked

                    if (which == 0 && isChecked) {
                        selectedCategories.clear()
                        selectedCategories.add("All")
                        for (i in 1 until checked.size) {
                            checked[i] = false
                            listView.setItemChecked(i, false)
                        }
                    } else {
                        if (isChecked) selectedCategories.add(categories[which])
                        else selectedCategories.remove(categories[which])

                        if (selectedCategories.isNotEmpty()) {
                            selectedCategories.remove("All")
                            checked[0] = false
                            listView.setItemChecked(0, false)
                        }

                        if (selectedCategories.isEmpty()) {
                            selectedCategories.add("All")
                            checked[0] = true
                            listView.setItemChecked(0, true)
                        }
                    }

                    applyCategoryMultiFilter(selectedCategories)
                }
                .setNegativeButton("OK", null)

            builder.create().show()
        }

        // Search Icon Click
        val searchInput: EditText = findViewById(R.id.search_input)
        searchInput.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {

                val startDrawable = searchInput.compoundDrawables[0]
                val endDrawable = searchInput.compoundDrawables[2]

                if (startDrawable != null) {
                    val iconWidth = startDrawable.bounds.width()
                    val touchAreaEnd = searchInput.paddingStart + iconWidth
                    if (event.x <= touchAreaEnd) {
                        applySearch(searchInput.text?.toString().orEmpty())
                        return@setOnTouchListener true
                    }
                }

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

    // Build displayed list from defaults + adminAdded
    private fun rebuildCoursesList() {
        courses.clear()
        courses.addAll(defaultCourses())
        courses.addAll(adminAdded)
        allCourses = courses.toList()
    }

    // Swipe delete only admin items
    private fun attachSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.adapterPosition
                    if (pos == RecyclerView.NO_POSITION) return

                    val item = adapter.getItemAt(pos)

                    //if default item-not delete, just restore row
                    if (isDefaultItem(item)) {
                        adapter.notifyItemChanged(pos)
                        return
                    }

                    //admin item-elete from adminAdded
                    adminAdded.removeAll {
                        it.title == item.title &&
                                it.description == item.description &&
                                it.category == item.category &&
                                it.videoUrl == item.videoUrl &&
                                (it.imageUrl ?: "") == (item.imageUrl ?: "")
                    }

                    if (adminAdded.isEmpty()) {
                        StorageManager.clearAddedItems(this@MainActivity)
                    } else {
                        StorageManager.saveAddedItems(this@MainActivity, adminAdded)
                    }

                    rebuildCoursesList()
                    adapter.updateItems(allCourses)
                }
            }
        )

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    // Default item detection
    private fun isDefaultItem(item: CourseItem): Boolean {
        return defaultCourses().any {
            it.title == item.title &&
                    it.description == item.description &&
                    it.category == item.category &&
                    it.videoUrl == item.videoUrl &&
                    it.imageRes == item.imageRes
        }
    }

    // Default items
    private fun defaultCourses(): ArrayList<CourseItem> = arrayListOf(
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
            val desTv: TextView = itemView.findViewById(R.id.tvCourseDescription)
            val imageIv: ImageView = itemView.findViewById(R.id.ivCourseImage)
            val playBtn: View = itemView.findViewById(R.id.btnPlay)
        }

        override fun getItemCount(): Int = items.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_course_row, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.titleTv.text = item.title
            holder.desTv.text = item.description

            if (!item.imageUrl.isNullOrBlank()) {
                com.squareup.picasso.Picasso.get()
                    .load(item.imageUrl)
                    .fit()
                    .centerCrop()
                    .into(holder.imageIv)
            } else {
                holder.imageIv.setImageResource(item.imageRes)
            }

            holder.playBtn.setOnClickListener {
                val url = item.videoUrl
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(Intent.createChooser(intent, "Open video with"))
            }
        }

        fun updateItems(newItems: List<CourseItem>) {
            items = newItems
            notifyDataSetChanged()
        }
        fun getItemAt(position: Int): CourseItem {
            return items[position]
        }
    }
}
