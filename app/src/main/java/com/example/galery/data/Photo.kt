package com.example.galery.data

import android.net.Uri

data class Photo(val id: Long,
                 val uri: Uri,
                 val name: String,
                 val date: String
                 )
