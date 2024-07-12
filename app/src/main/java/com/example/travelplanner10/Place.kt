package com.example.travelplanner10


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.work.impl.StartStopToken

@Entity(tableName = "places")
data class Place(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val name: String,
    val description: String,
    val image: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val latitude: Double,
    val longitude: Double,
//  val timestamp: Long
)
