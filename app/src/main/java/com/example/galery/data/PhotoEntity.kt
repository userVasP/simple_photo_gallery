package com.example.galery.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_photo_table")
data class PhotoEntity(@PrimaryKey @ColumnInfo(name = "key") val key: String)