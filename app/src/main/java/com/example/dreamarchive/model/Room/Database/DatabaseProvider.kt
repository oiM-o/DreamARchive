package com.example.dreamarchive.model.Room.Database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "dream_archive_db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}