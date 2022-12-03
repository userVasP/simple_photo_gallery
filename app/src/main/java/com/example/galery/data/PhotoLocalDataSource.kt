package com.example.galery.data

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.galery.api.PhotoServerApi
import com.example.galery.data.CollectionUri.getCollectionUri
import com.example.galery.data.model.Photo
import com.example.galery.utilities.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.inject.Inject

class PhotoLocalDataSource @Inject constructor(private val context: Context,  private val photoDAO: PhotoDAO) {
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
                MediaStore.Images.Media.DATE_ADDED,
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
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

                while (cursor.moveToNext()) {

                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val dateValue = cursor.getLong(dateColumn)


                    val date = Date(dateValue * 1000).toString()

                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    photoList += Photo(id, contentUri, name, date)
                }
            }
        }
        return photoList
    }

    suspend fun deletePhoto(imageUri:Uri) {
        withContext(Dispatchers.IO) {
            contentResolver.delete(
                imageUri, null, null)
        }

    }

    suspend fun deleteFavoritePhoto(photoEntity: PhotoEntity) {
        photoDAO.delete(photoEntity)
    }

    fun getAllPhoto(): List<PhotoEntity> {
        return photoDAO.getAllFavoritePhoto()
    }

    suspend fun insertPhoto(namesPhoto: List<String>, cachedPhoto: List<Photo>?) {
        val namesFilteredPhoto = namesPhoto.filter f@ {
            for (ph in cachedPhoto!!) {
                if (ph.name == it) {
                    return@f false
                }
            }
            return@f true
        }

        for (name in namesFilteredPhoto) {
            val uriString = "${PhotoServerApi.BASE_URL}/${name}"
            val uri: Uri = Uri.parse(uriString)
            val bitmap = Utils.getBitmap(context, uri)
            saveBitmap(context, bitmap, Bitmap.CompressFormat.JPEG, "image/jpeg", name)
        }
    }


    @Throws(IOException::class)
    fun saveBitmap(
        context: Context, bitmap: Bitmap, format: Bitmap.CompressFormat,
        mimeType: String, displayName: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                put(MediaStore.Images.Media.DATE_ADDED, (System.currentTimeMillis()/1000))
            }
            val resolver = context.contentResolver
            var uri: Uri? = null
            try {
                uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: throw IOException("Failed to create new MediaStore record.")

                resolver.openOutputStream(uri)?.use {
                    if (!bitmap.compress(format, 100, it))
                        throw IOException("Failed to save bitmap.")
                } ?: throw IOException("Failed to open output stream.")

            } catch (e: IOException) {

                uri?.let { orphanUri ->

                    resolver.delete(orphanUri, null, null)
                }

                throw e
            }
        }
        else {
            val imagesDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString())
            if (!imagesDir.exists())
                imagesDir.mkdir()
            val imageFile = File(imagesDir, "$displayName.jpg")
            try {
                val fos = FileOutputStream(imageFile)
                fos.use {
                    if (!bitmap.compress(format, 100, it))
                        throw IOException("Failed to save bitmap.")
                }
            }
            catch (e: IOException) {
                imageFile.delete()
            }
        }
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