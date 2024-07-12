package com.example.travelplanner10

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.travelplanner10.EditPlaceActivity.Activity

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities WHERE placeId = :placeId")
    suspend fun getActivitiesForPlace(placeId: Int): List<Activity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: Activity)

    @Update
    suspend fun update(activity: Activity)

    @Query("DELETE FROM activities WHERE id = :id")
    suspend fun delete(id: Int)

}


