package com.example.galery.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

class Utils {
    companion object {
        suspend fun getBitmap(context: Context, uri: Uri): Bitmap {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(uri)
                .allowHardware(false)
                .build()

            val result = (loader.execute(request) as SuccessResult).drawable
            return (result as BitmapDrawable).bitmap
        }
    }
}