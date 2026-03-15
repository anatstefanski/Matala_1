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
    fun submitList(newList: List<StudySession>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_session_row, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(session: StudySession) {
            itemView.findViewById<TextView>(R.id.tvSessionTopic).text = session.topic
            itemView.findViewById<TextView>(R.id.tvSessionDate).text = "${session.date} | ${session.time}"

            val cb = itemView.findViewById<CheckBox>(R.id.cbRegister)
            if(courseId.isEmpty()){
                cb.visibility = View.GONE
            }
            cb.setOnCheckedChangeListener(null)
            cb.isChecked = session.isUserRegistered
            cb.setOnCheckedChangeListener { _, isChecked ->
                onRegClick(session, isChecked)
            }

            itemView.findViewById<TextView>(R.id.tvLocation).setOnClickListener {
                onLocClick(session)
            }
            itemView.setOnClickListener {
                onSessionClick(session)
            }
        }
    }
}