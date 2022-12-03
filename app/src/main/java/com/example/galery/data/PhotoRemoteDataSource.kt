package com.example.galery.data

import android.content.Context
import android.graphics.Bitmap
import com.example.galery.api.PhotoServerApi
import com.example.galery.utilities.Utils
import com.example.galery.data.model.LoggedInUser
import com.example.galery.data.model.Photo
import com.example.galery.data.model.Result
import com.example.galery.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*
import javax.inject.Inject

class PhotoRemoteDataSource @Inject constructor(private val context: Context, private val photoServerApi: PhotoServerApi) {

    suspend fun login(user: User): Result<LoggedInUser> {
        return try {
            val res = photoServerApi.login(user)
            if (res.isSuccessful) {
                val userResult = res.body()
                Result.Success(userResult!!)
            } else {
                Result.Error(IOException(res.message()))
            }

        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }

    suspend fun logout(user: LoggedInUser?) {
        try {
            photoServerApi.logout("Bearer ${user!!.accessToken}", user )
        }
        catch (e: Throwable) {
            println(e.message)
        }

    }

    suspend fun registration(user: User): Result<Unit> {
        return try {
            val res = photoServerApi.postUser(user)
            if (res.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(IOException(res.message()))
            }

        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }


    suspend fun fetchPhotoFromServer(token: String, user: LoggedInUser): List<String> {

        try {
            val resp = photoServerApi.fetchPhoto("Bearer $token", user.userId.toInt())
            if (resp.isSuccessful) {
                return resp.body()!!.namesImages
            }
        }
        catch (e: Throwable) {
            println(e.message)
            return listOf()
        }
        return listOf()
    }

    suspend fun sendPhotoToServer(token: String, photo: List<Photo>, user: LoggedInUser) {
        for (ph in photo) {
            val file = File(context.cacheDir, ph.name)
            withContext(Dispatchers.IO) {
                file.createNewFile()
            }

            val bitmap = Utils.getBitmap(context, ph.uri)
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            val bitmapData = bos.toByteArray()
            var fos: FileOutputStream? = null
            try {
                fos = withContext(Dispatchers.IO) {
                    FileOutputStream(file)
                }
            }
            catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            try {
                withContext(Dispatchers.IO) {
                    fos!!.write(bitmapData)
                    fos.flush()
                    fos.close()
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }

            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("upload", file.name, reqFile)
            val headers = HashMap<String, String>()
            headers["Authorization"] = "Bearer $token"

            try {
                photoServerApi.sendPhoto(headers, body, user.userId.toInt())
            }
            catch (e: Throwable) {
                println(e.message)
            }
        }
    }
}