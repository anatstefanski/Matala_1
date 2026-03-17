package com.example.first_exercise

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.first_exercise.model.ChatMessage
import com.example.first_exercise.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
// ChatActivity.kt
class ChatActivity : AppCompatActivity() {
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var sessionId: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        sessionId = intent.getStringExtra("SESSION_ID") ?: ""
        userId = intent.getStringExtra("USER_ID") ?: ""

        val adapter = ChatAdapter(userId)
        val rv = findViewById<RecyclerView>(R.id.rvMessages)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        viewModel.messages.observe(this) { list ->
            adapter.submitList(list)
            rv.scrollToPosition(list.size - 1) // גלילה לסוף בהודעה חדשה
        }
        val etMessage = findViewById<EditText>(R.id.etMessage)
        val btnSend = findViewById<Button>(R.id.btnSend)

        viewModel.observeMessages(sessionId)

        btnSend.setOnClickListener {
            val text = etMessage.text.toString()
            if (text.isNotBlank()) {
                viewModel.sendMessageWithUserName(sessionId, userId, text)
                etMessage.text.clear()
            }
        }

        // סימון כנקרא בכניסה
        viewModel.markChatAsRead(userId, sessionId)
    }
}