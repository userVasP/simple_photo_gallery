package com.example.galery.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.galery.data.Photo
import com.example.galery.data.PhotoLocalDataSource
import com.example.galery.databinding.GaleryFragmentItemBinding


class PictureRecyclerViewAdapter : ListAdapter<Photo, PictureRecyclerViewAdapter.ViewHolder>(PhotoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            GaleryFragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = getItem(position)
        if (photo != null) {
            holder.bind(photo)
        }
    }

    inner class ViewHolder(private  val binding: GaleryFragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Photo) {
            binding.apply {
                photo = item
                executePendingBindings()
            }
        }

    }
}

private class PhotoDiffCallback : DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem == newItem
    }
}