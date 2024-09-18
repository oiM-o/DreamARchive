package com.example.dreamarchive.model.Room.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.dreamarchive.model.Room.Entity.Photo

@Dao
interface PhotoDao {
    @Insert
    suspend fun insert(photo: Photo)

    @Query("SELECT * FROM photos ORDER BY timestamp DESC")
    suspend fun getAllPhotos(): List<Photo>
}