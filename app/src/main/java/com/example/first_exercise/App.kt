package com.example.first_exercise

import android.app.Application

/**
 * Custom Application class used to provide a global application context.
 * Allows access to context from anywhere in the app.
 */
class App : Application() {
    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}