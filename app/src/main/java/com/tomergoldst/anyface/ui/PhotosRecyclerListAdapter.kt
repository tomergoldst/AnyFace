package com.tomergoldst.anyface.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tomergoldst.anyface.R
import com.tomergoldst.anyface.model.Photo

class PhotosRecyclerListAdapter(val mListener: OnAdapterInteractionListener? = null) :
    ListAdapter<Photo, PhotosRecyclerListAdapter.ImageBoxViewHolder>(DiffCallback()){

    companion object {
        val TAG: String = PhotosRecyclerListAdapter::class.java.simpleName
    }

    interface OnAdapterInteractionListener {
        fun onImageClicked(photo: Photo)
    }

    inner class ImageBoxViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var imageView: AppCompatImageView = v.findViewById(R.id.imageView)

        fun bind(photo: Photo){
            itemView.setOnClickListener{
                mListener?.onImageClicked(photo)
            }

            Glide.with(itemView.context)
                .load(photo.path)
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageBoxViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo_card, parent, false)
        return ImageBoxViewHolder(v)
    }

    override fun onBindViewHolder(holder: ImageBoxViewHolder, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
    }

    class DiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }

}
