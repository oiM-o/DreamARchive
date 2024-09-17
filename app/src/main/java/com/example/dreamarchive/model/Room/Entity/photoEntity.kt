package com.example.dreamarchive.model.Room.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val filePath: String,
    val timestamp: Long = System.currentTimeMillis()
)