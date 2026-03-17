package com.example.first_exercise.repository

import com.example.first_exercise.model.ChatMessage
import com.google.firebase.firestore.FirebaseFirestore
class ChatRepository {

    private val db = FirebaseFirestore.getInstance()

    fun sendMessage(sessionId: String, message: ChatMessage, onComplete: (Boolean) -> Unit) {
        val id = db.collection("session").document(sessionId)
            .collection("messages").document().id
        val msgWithId = message.copy(messageId = id)
        db.collection("session").document(sessionId)
            .collection("messages").document(id)
            .set(msgWithId)
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }

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
    }

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
                        // סינון: ספור רק הודעות שלא אני שלחתי
                        val unread = msgSnap.documents.count {
                            it.getString("senderId") != userId
                        }
                        onResult(unread)
                    }
            }
    }
}