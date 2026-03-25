package com.example.first_exercise.repository

import com.example.first_exercise.model.ChatMessage
import com.google.firebase.firestore.FirebaseFirestore
/**
 * Handles all chat operations with Firebase:
 * - Sending messages
 * - Listening to real-time updates
 * - Managing read/unread messages
 */
class ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    /**
     * Sends a message to Firebase under:
     * session/{sessionId}/messages
     */
    fun sendMessage(sessionId: String, message: ChatMessage, onComplete: (Boolean) -> Unit) {
        val id = db.collection("session").document(sessionId)
            .collection("messages").document().id
        val msgWithId = message.copy(messageId = id)
        db.collection("session").document(sessionId)
            .collection("messages").document(id)
            .set(msgWithId)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    /**
     * Listens for real-time updates of messages in a session.
     * Automatically updates when new messages are added.
     */
    fun observeMessages(sessionId: String, onUpdate: (List<ChatMessage>) -> Unit) {
        db.collection("session").document(sessionId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(ChatMessage::class.java) ?: emptyList()
                onUpdate(messages)
            }
    }

    /**
     * Marks all messages in a session as read for a specific user.
     * Updates lastReadTimestamp in attendance collection.
     */
    fun markChatAsRead(userId: String, sessionId: String) {
        db.collection("attendance")
            .whereEqualTo("userId", userId)
            .whereEqualTo("sessionId", sessionId)
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    doc.reference.update("lastReadTimestamp", System.currentTimeMillis())
                }
            }
            .addOnFailureListener {
                // Failed to update read status
            }
    }

    /**
     * Calculates unread messages for a session:
     * - Gets last read timestamp
     * - Counts messages sent after that
     * - Ignores messages sent by the current user
     */
    fun getUnreadCount(userId: String, sessionId: String, onResult: (Int) -> Unit) {
        db.collection("attendance")
            .whereEqualTo("userId", userId)
            .whereEqualTo("sessionId", sessionId)
            .get()
            .addOnSuccessListener { attendanceSnap ->
                val lastRead = attendanceSnap.documents.firstOrNull()?.getLong("lastReadTimestamp") ?: 0L

                db.collection("session").document(sessionId)
                    .collection("messages")
                    .whereGreaterThan("timestamp", lastRead)
                    .get()
                    .addOnSuccessListener { msgSnap ->
                        val unread = msgSnap.documents.count {
                            it.getString("senderId") != userId
                        }
                        onResult(unread)
                    }
                    .addOnFailureListener {
                        onResult(0)
                    }
            }
            .addOnFailureListener {
                onResult(0)
            }

    }
}