package com.example.travelplanner10

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ActivityAdapter(
    val activities: MutableList<EditPlaceActivity.Activity>,
    private val onDeleteClickListener: (EditPlaceActivity.Activity) -> Unit
): RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activities[position]
        holder.name.text = activity.name
        holder.date.text = activity.date
        holder.description.text = activity.description

        holder.buttonDelete.setOnClickListener {
            onDeleteClickListener(activity) // passo l'activity da eliminare al listener
        }
    }
    override fun getItemCount(): Int {
        return activities.size
    }
    fun updateActivities(neWactivities: List<EditPlaceActivity.Activity>) {
        activities.clear()
        activities.addAll(neWactivities)
        notifyDataSetChanged()
    }

    fun deleteActivity(activity: EditPlaceActivity.Activity) {
        val position = activities.indexOf(activity)
        if (position != -1) {
            activities.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textViewActivityName)
        val date: TextView = itemView.findViewById(R.id.textViewActivityDate)
        val description: TextView = itemView.findViewById(R.id.textViewActivityDescription)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDeleteActivity)
    }
}