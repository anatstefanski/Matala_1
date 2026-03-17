package com.example.first_exercise.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.first_exercise.model.ChatMessage
import com.example.first_exercise.repository.AuthRepository
import com.example.first_exercise.repository.ChatRepository
class ChatViewModel(private val repo: ChatRepository = ChatRepository()) : ViewModel() {
    private val authRepo = AuthRepository()
    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages
    // ChatViewModel.kt


    fun sendMessageWithUserName(sessionId: String, userId: String, text: String) {
        authRepo.getUserName(userId) { name ->
            val newMessage = ChatMessage(
                senderId = userId,
                senderName = name,
                text = text,
                timestamp = System.currentTimeMillis()
            )
            sendMessage(sessionId, newMessage)
        }
    }
    fun sendMessage(sessionId: String, message: ChatMessage) {
        repo.sendMessage(sessionId, message) { success ->
            if (!success) Log.e("ChatViewModel", "Failed to send message")
        }
    }

    fun observeMessages(sessionId: String) {
        repo.observeMessages(sessionId) { list ->
            _messages.postValue(list)
        }
    }

    fun markChatAsRead(userId: String, sessionId: String) {
        repo.markChatAsRead(userId, sessionId)
    }
}