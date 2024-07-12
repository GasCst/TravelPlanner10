package com.example.travelplanner10


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class PlaceAdapter(
    val places: MutableList<Place>,
    private val onDeleteClickListener: (Int) -> Unit,
    private val onModifyClickListener: (Int) -> Unit
    ): RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.nameTextView.text = place.name
        holder.descriptionTextView.text = place.description
        holder.date.text = place.date
        holder.startTime.text = place.startTime
        holder.endTime.text = place.endTime

        Log.d("PlaceAdapter", "Loading image: ${place.image}")
        // utilizzo Glide per prendere l'url dell'immagine
        Glide.with(holder.itemView.context)
            .load(place.image) // Load image from URL
            .error(R.drawable.error_image) // Optional error image
            .into(holder.imageView1)

        holder.deleteButton.setOnClickListener {
            onDeleteClickListener(place.id) // Pass the place ID for deletion
        }
        holder.modifyButton.setOnClickListener {
            onModifyClickListener(place.id)
        }
    }

    override fun getItemCount() = places.size

    fun updatePlaces(newPlaces: List<Place>) {
        places.clear()
        places.addAll(newPlaces)
        notifyDataSetChanged()
    }

    class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val imageView1: ImageView = itemView.findViewById(R.id.imageView1)

        val date: TextView = itemView.findViewById(R.id.dateTextView)
        val startTime: TextView = itemView.findViewById(R.id.startTime)
        val endTime: TextView = itemView.findViewById(R.id.endTime)

        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        val modifyButton: Button = itemView.findViewById(R.id.modifyButton)
    }
}
