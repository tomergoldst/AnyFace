package com.tomergoldst.anyface.data

import androidx.lifecycle.LiveData
import com.tomergoldst.anyface.model.Photo


interface DataSource {

    fun getPhotosSync(type: Int): List<Photo>

    fun savePhotos(photos: List<Photo>)

    fun updatePhotos(photos: List<Photo>)

    fun getPhotosLiveData(type: Int): LiveData<List<Photo>>

    fun deletePhoto(photo: Photo)

    fun clear()

}
