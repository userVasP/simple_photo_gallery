package com.example.galery.data

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class Photo(val id: Long,
                 val uri: Uri,
                 val name: String,
                 val date: String) {
    fun getKey(): String {
        return name + date
    }
}

@Entity(tableName = "favorite_photo_table")
data class PhotoEntity(@PrimaryKey @ColumnInfo(name = "key") val key: String)
