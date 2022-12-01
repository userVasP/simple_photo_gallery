package com.example.galery.adapters

import android.net.Uri
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, url: Uri?) {
    if (url != null) {
        view.load(url) {
            crossfade(true)
        }
    }
}

@BindingAdapter("app:setFieldError")
fun setFieldError(editText: EditText, error: Int?) {
    editText.error = error?.let { editText.context.applicationContext.getString(it) }
}

