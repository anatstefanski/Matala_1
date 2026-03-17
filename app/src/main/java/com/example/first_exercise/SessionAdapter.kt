package com.example.first_exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.first_exercise.model.StudySession

class SessionAdapter(
    private val courseId: String,
    private val onRegClick: (StudySession, Boolean) -> Unit,
    private val onLocClick: (StudySession) -> Unit,
    private val onSessionClick: (StudySession) -> Unit
) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    private var items = listOf<StudySession>()
    private var unreadCounts = mapOf<String, Int>()

    fun updateUnreadCounts(newCounts: Map<String, Int>) {
        unreadCounts = newCounts
        notifyDataSetChanged()
    }
    fun submitList(newList: List<StudySession>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_session_row, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = items[position]
        val count = unreadCounts[session.sessionId] ?: 0
        holder.bind(session, count) // שליחת ה-count לפונקציית ה-bind הקיימת
    }

    override fun getItemCount() = items.size

    inner class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(session: StudySession,unreadCount: Int = 0) {
            itemView.findViewById<TextView>(R.id.tvSessionTopic).text = session.topic
            itemView.findViewById<TextView>(R.id.tvSessionDate).text = "${session.date} | ${session.time}"

            val cb = itemView.findViewById<CheckBox>(R.id.cbRegister)
            if (courseId.isEmpty()) {
                cb.visibility = View.GONE
            }

            cb.setOnCheckedChangeListener(null)
            cb.isChecked = session.isUserRegistered
            cb.setOnCheckedChangeListener { _, isChecked ->
                onRegClick(session, isChecked)
            }

            val tvLocation = itemView.findViewById<TextView>(R.id.tvLocation)
            if (session.latitude == 0.0 && session.longitude == 0.0) {
                tvLocation.text = "No location available"
                tvLocation.isEnabled = false
                tvLocation.alpha = 0.5f
            } else {
                tvLocation.text = "📍Navigation to the session"
                tvLocation.isEnabled = true
                tvLocation.alpha = 1f
                tvLocation.setOnClickListener {
                    onLocClick(session)
                }
            }

            itemView.setOnClickListener {
                onSessionClick(session)
            }
            val badge = itemView.findViewById<TextView>(R.id.tvSessionBadge)
            if (unreadCount > 0) {
                badge.visibility = View.VISIBLE
                badge.text = unreadCount.toString()
            } else {
                badge.visibility = View.GONE
            }
        }
    }
}