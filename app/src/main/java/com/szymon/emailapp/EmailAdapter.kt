package com.szymon.emailapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmailAdapter(
    private val items: List<EmailItem>,
    private val onClick: (EmailItem) -> Unit
) : RecyclerView.Adapter<EmailAdapter.ViewHolder>() {

    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val from: TextView = view.findViewById(R.id.fromText)
        val subject: TextView = view.findViewById(R.id.subjectText)
        val date: TextView = view.findViewById(R.id.dateText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_email, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.from.text = item.from
        holder.subject.text = item.subject
        holder.date.text = item.date
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}
