package com.example.dreamarchive.model.Room.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dreamarchive.model.Room.Dao.PhotoDao
import com.example.dreamarchive.model.Room.Entity.Photo

@Database(entities = [Photo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}