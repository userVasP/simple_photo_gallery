package com.example.galery.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import coil.ImageLoader
import coil.executeBlocking
import coil.request.ImageRequest
import coil.request.SuccessResult

class Utils {
    companion object {

        fun getBitmap(context: Context, uri: Uri): Bitmap {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(uri)
                .allowHardware(false)
                .build()

            val result = loader.executeBlocking(request)
            return (result.drawable as BitmapDrawable).bitmap
        }
    }
}