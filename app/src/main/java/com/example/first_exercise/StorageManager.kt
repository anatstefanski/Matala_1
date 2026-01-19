package com.example.first_exercise

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Manages saving and loading the course list using SharedPreferences (JSON format)
object StorageManager {
    private const val PREFS_NAME = "catalog_prefs"
    private const val KEY_ITEMS = "items_json"
    private val gson = Gson()

    fun saveItems(context: Context, items: List<CourseItem>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_ITEMS, gson.toJson(items)).apply()
    }

    fun loadItems(context: Context): ArrayList<CourseItem>? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_ITEMS, null) ?: return null
        val type = object : TypeToken<ArrayList<CourseItem>>() {}.type
        return gson.fromJson(json, type)
    }
}
