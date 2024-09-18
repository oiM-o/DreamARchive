package com.example.dreamarchive.ui.screen.ar

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dreamarchive.model.Room.Database.DatabaseProvider
import com.example.dreamarchive.model.Room.Entity.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ArViewModel(application: Application) : AndroidViewModel(application)  {
    private val photoDao = DatabaseProvider.getDatabase(application).photoDao()

    fun captureAndSavePhoto(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                // 画像を内部ストレージに保存
                val photoFile = saveBitmapToFile(getApplication(), bitmap)

                // Roomデータベースにファイルパスを保存
                val photo = Photo(filePath = photoFile.absolutePath)
                photoDao.insert(photo)

                Log.d("ARViewModel", "Photo saved successfully: ${photoFile.absolutePath}")
            } catch (e: Exception) {
                Log.e("ARViewModel", "Failed to capture photo: ${e.message}", e)
                // エラーハンドリング（ユーザーへの通知など）
            }
        }
    }

    private suspend fun saveBitmapToFile(context: Context, bitmap: Bitmap): File = withContext(Dispatchers.IO) {
        val filename = "photo_${System.currentTimeMillis()}.png"
        val file = File(context.filesDir, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        file
    }
}