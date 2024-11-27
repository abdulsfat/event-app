package com.example.myapplication.ui.common

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.response.ListEventsItem
import com.example.myapplication.database.Favorite

class EventsAdapter<T>(
    private var listEvents: List<T>,
    private val listener: (T) -> Unit
) : RecyclerView.Adapter<EventsAdapter<T>.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventName: TextView = view.findViewById(R.id.tvEventName)
        val eventImage: ImageView = view.findViewById(R.id.ivEventImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = listEvents[position]

        // ini buat nampilin yang dari API
        if (event is ListEventsItem) {
            holder.eventName.text = event.name
            Glide.with(holder.itemView.context)
                .load(event.imageLogo)
                .into(holder.eventImage)
        }

        // ini buat nampilin yang dari Database
        if (event is Favorite) {
            holder.eventName.text = event.name
            Glide.with(holder.itemView.context)
                .load(event.mediaCover)
                .into(holder.eventImage)
        }

        holder.itemView.setOnClickListener {
            listener(event)
        }
    }

    override fun getItemCount(): Int = listEvents.size

    // Update daftar event
    @SuppressLint("NotifyDataSetChanged")
    fun updateEvents(newEvents: List<T>) {
        listEvents = newEvents
        notifyDataSetChanged()
    }
}
