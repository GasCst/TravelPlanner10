package com.example.travelplanner10

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places")
    suspend fun getAll(): List<Place>

    @Query("SELECT * FROM places WHERE id = :id")
    suspend fun getById(id: Int): Place

    @Insert
    suspend fun insert(place: Place)

    @Query("DELETE FROM places WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE places SET name = :name, description = :description, image = :image, date = :date, startTime = :startTime, endTime = :endTime WHERE id = :id")
    suspend fun update(id: Int, name: String, description: String, image: String, date: String, startTime: String, endTime: String)

}
