package com.example.first_exercise

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.first_exercise.viewmodel.ChatViewModel

/**
 * Chat screen.
 * Displays messages and allows sending new messages.
 */
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
            rv.scrollToPosition(list.size - 1)
        }
        val etMessage = findViewById<EditText>(R.id.etMessage)
        val btnSend = findViewById<Button>(R.id.btnSend)

        viewModel.observeMessages(sessionId)

        //send button
        btnSend.setOnClickListener {
            val text = etMessage.text.toString()
            if (text.isBlank()) {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.sendMessageWithUserName(sessionId, userId, text) { success ->
                if (success) {
                    etMessage.text.clear()
                } else {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Mark messages as read when entering chat
        viewModel.markChatAsRead(userId, sessionId)
    }
}