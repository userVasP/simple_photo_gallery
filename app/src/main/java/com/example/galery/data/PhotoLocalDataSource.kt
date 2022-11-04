package com.example.galery.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.galery.data.CollectionUri.getCollectionUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class PhotoLocalDataSource @Inject constructor(context: Context,  private val photoDAO: PhotoDAO) {
    private val contentResolver = context.contentResolver

    fun getAllFavoritePhoto(): Flow<List<PhotoEntity>> {
        return photoDAO.getAllFavoritePhotoAsFlow()
    }

    suspend fun insertFavoritePhoto(photoEntity: PhotoEntity) {
        photoDAO.insert(photoEntity)
    }

    suspend fun checkPhoto(key: String): Boolean {
        return photoDAO.searchPhoto(key).isNotEmpty()
    }

    suspend fun getPhotoLocal(): MutableList<Photo> {
        val photoList = mutableListOf<Photo>()
        withContext(Dispatchers.IO) {


            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
            )

            val query = contentResolver.query(
                getCollectionUri(),
                projection,
                null,
                null,
                null
            )
            query?.use { cursor ->

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

                while (cursor.moveToNext()) {

                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val dateValue = cursor.getLong(dateColumn)
                    val date = Date(dateValue).toString()

                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    photoList += Photo(id, contentUri, name, date.toString())
                }
            }
        }
        return photoList
    }

    suspend fun deletePhoto(imageUri:Uri) {
        withContext(Dispatchers.IO) {
            val numImagesRemoved = contentResolver.delete(
                imageUri, null, null)
        }

    }

    suspend fun deleteFavoritePhoto(photoEntity: PhotoEntity) {
        photoDAO.delete(photoEntity)
    }

    fun getAllPhoto(): List<PhotoEntity> {
        return photoDAO.getAllFavoritePhoto()
    }


}

object CollectionUri {
    fun getCollectionUri(): Uri {
        val collectionUri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        return collectionUri
    }
}