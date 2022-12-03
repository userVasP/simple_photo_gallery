package com.example.galery.api

import com.example.galery.data.model.User
import com.example.galery.data.model.PhotoResponse
import com.example.galery.data.model.LoggedInUser
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PhotoServerApi {


    @GET("GetPhoto/{userId}")
    suspend fun fetchPhoto (@Header("Authorization") token: String, @Path ("userId") userId: Int): Response<PhotoResponse>

    @Multipart
    @POST("Photo")
    suspend fun sendPhoto (
        @HeaderMap token: Map<String, String>,
        @Part image: MultipartBody.Part,
        @Part("userId") user: Int
    ): Response<Unit>

    @POST("Login")
    suspend fun login(@Body user: User): Response<LoggedInUser>

    @POST("Registration")
     suspend fun postUser(@Body user: User): Response<Unit>

    @POST("Logout")
    suspend fun logout(@Header("Authorization") token: String, @Body loggedInUser: LoggedInUser)

    companion object {
        const val BASE_URL = "http://10.0.2.2:5058"
    }
}