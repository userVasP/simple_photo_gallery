package com.example.galery.api

import com.example.galery.data.model.User
import com.example.galery.data.model.PhotoResponse
import com.example.galery.data.model.LoggedInUser
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PhotoServerApi {


    @GET("GetPhoto/{userId}")
    fun fetchPhoto (@Header("Authorization") token: String, @Path ("userId") userId: Int): Single<Response<PhotoResponse>>

    @Multipart
    @POST("Photo")
    fun sendPhoto (
        @HeaderMap token: Map<String, String>,
        @Part image: MultipartBody.Part,
        @Part("userId") user: Int
    ): Completable

    @POST("Login")
    fun login(@Body user: User): Single<Response<LoggedInUser>>

    @POST("Registration")
    fun postUser(@Body user: User): Completable

    @POST("Logout")
    fun logout(@Header("Authorization") token: String, @Body loggedInUser: LoggedInUser): Completable

    companion object {
        const val BASE_URL = "http://10.0.2.2:5058"
    }
}