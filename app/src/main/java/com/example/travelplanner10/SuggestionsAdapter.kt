package com.example.travelplanner10

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SuggestionsAdapter(
    private val suggestions: List<GeoName>,
    private val onSuggestionClick: (GeoName) -> Unit
    ) : RecyclerView.Adapter<SuggestionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val suggestion = suggestions[position]
        holder.textView.text = suggestion.name
        holder.itemView.setOnClickListener {
            onSuggestionClick(suggestion)
        }
    }

    override fun getItemCount(): Int = suggestions.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(android.R.id.text1)
    }
}
