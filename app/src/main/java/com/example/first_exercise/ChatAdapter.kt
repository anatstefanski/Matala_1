package com.example.first_exercise

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.first_exercise.model.ChatMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter for displaying chat messages in RecyclerView.
 * Responsible for showing messages on left/right depending on sender.
 */
class ChatAdapter(private val userId: String) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private var messages: List<ChatMessage> = emptyList()

    /**
     *Updates the list of messages and refreshes UI
     */
    fun submitList(list: List<ChatMessage>) {
        messages = list
        notifyDataSetChanged()
    }

    /**
     * Creates a new message view (bubble)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position], userId)
    }

    override fun getItemCount(): Int = messages.size

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(msg: ChatMessage, userId: String) {
            val tvMsg = itemView.findViewById<TextView>(R.id.tvMessage)
            val tvTime = itemView.findViewById<TextView>(R.id.tvTime)
            val tvSenderName = itemView.findViewById<TextView>(R.id.tvSenderName) // חדש
            val container = itemView.findViewById<LinearLayout>(R.id.messageContainer)
            val bubble = itemView.findViewById<LinearLayout>(R.id.messageBubble)

            // Sets message text and time
            tvMsg.text = msg.text
            tvTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp))

            if (msg.senderId == userId) {
                container.gravity = Gravity.END
                bubble.setBackgroundResource(R.drawable.my_message_background)
                tvSenderName.visibility = View.GONE // אל תראה את השם שלי
            } else {
                container.gravity = Gravity.START
                bubble.setBackgroundResource(R.drawable.other_message_background)
                tvSenderName.visibility = View.VISIBLE
                tvSenderName.text = if (msg.senderName.isNotEmpty()) msg.senderName else "Student"
            }
        }
    }
}