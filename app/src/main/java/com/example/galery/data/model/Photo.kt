package com.example.galery.data.model

import android.net.Uri

data class Photo(val id: Long,
                 val uri: Uri,
                 val name: String,
                 val date: String) {
    fun getKey(): String {
        return name + date
    }
}


