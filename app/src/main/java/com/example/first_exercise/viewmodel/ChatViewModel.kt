package com.example.first_exercise.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.first_exercise.model.ChatMessage
import com.example.first_exercise.repository.AuthRepository
import com.example.first_exercise.repository.ChatRepository
/**
 * Responsible for:
 * Sending messages
 * Loading real-time messages
 * Marking messages as read
 */
class ChatViewModel(private val repo: ChatRepository = ChatRepository()) : ViewModel() {
    private val authRepo = AuthRepository()
    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    /**
     * Sends a message with the user's name.
     * First fetches the name, then sends the message.
     */
    fun sendMessageWithUserName(sessionId: String, userId: String, text: String,onResult: (Boolean) -> Unit) {
        authRepo.getUserName(userId) { name ->
            val newMessage = ChatMessage(
                senderId = userId,
                senderName = name,
                text = text,
                timestamp = System.currentTimeMillis()
            )
            sendMessage(sessionId, newMessage, onResult)
        }
    }

    /**
     * Sends message to Firebase via repository
     */
    fun sendMessage(sessionId: String, message: ChatMessage, onResult: (Boolean) -> Unit) {
        repo.sendMessage(sessionId, message) { success ->

            if (!success) {
                Log.e("ChatViewModel", "Failed to send message")
            }
            onResult(success)
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