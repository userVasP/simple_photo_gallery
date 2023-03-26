package com.example.galery.data

import android.content.Context
import android.graphics.Bitmap
import com.example.galery.api.PhotoServerApi
import com.example.galery.utilities.Utils
import com.example.galery.data.model.LoggedInUser
import com.example.galery.data.model.Photo
import com.example.galery.data.model.Result
import com.example.galery.data.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.*
import javax.inject.Inject

class PhotoRemoteDataSource @Inject constructor(@ApplicationContext private val context: Context, private val photoServerApi: PhotoServerApi) {

    fun login(user: User): Single<LoggedInUser> {
        return photoServerApi.login(user).map { res -> if (res.isSuccessful) {
                res.body()
            }
            else throw Exception(res.message())
        }
    }

    fun logout(user: LoggedInUser?): Completable {
        return photoServerApi.logout("Bearer ${user!!.accessToken}", user).doOnError { println(it.message) }
    }


    fun registration(user: User): Completable {
        return photoServerApi.postUser(user)
    }


    fun fetchPhotoFromServer(token: String, user: LoggedInUser): Single<List<String>> {
        return photoServerApi.fetchPhoto("Bearer $token", user.userId.toInt())
            .map {
            if (it.isSuccessful) {
                it.body()!!.namesImages
            }
            else throw Exception(it.message())
        }
    }

    fun sendPhotoToServer(token: String, photo: List<Photo>, user: LoggedInUser): Completable {
        val headers = HashMap<String, String>()
        headers["Authorization"] = "Bearer $token"

        return Observable.create<MultipartBody.Part> {

        for (ph in photo) {
            if(it.isDisposed)
            {
                break
            }
            val file = File(context.cacheDir, ph.name)
            file.createNewFile()

            val bitmap = Utils.getBitmap(context, ph.uri)
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            val bitmapData = bos.toByteArray()
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
            }
            catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            try {
                fos!!.write(bitmapData)
                fos.flush()
                fos.close()

            } catch (e: IOException) {
                e.printStackTrace()
            }

            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("upload", file.name, reqFile)
            it.onNext(body)
        }
            it.onComplete()
        }.flatMapCompletable {
            photoServerApi.sendPhoto(headers, it, user.userId.toInt())
        }
    }
}