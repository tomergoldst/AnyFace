package com.tomergoldst.anyface.data

import androidx.lifecycle.LiveData
import com.tomergoldst.anyface.config.Config
import com.tomergoldst.anyface.model.Photo

class Repository private constructor(
    private val photosDao: PhotosDao
) : DataSource {

    companion object {

        private val TAG = Repository::class.java.simpleName

        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(
            photosDao: PhotosDao
        ):
                Repository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Repository(photosDao).also { INSTANCE = it }
            }

    }

    override fun getPhotosSync(type: Int): List<Photo> {
        return when (type) {
            Config.TYPE_ALL -> photosDao.getAllSync()
            Config.TYPE_WITH_FACES -> photosDao.getAllSync(1)
            Config.TYPE_WITHOUT_FACES -> photosDao.getAllWithoutFacesSync()
            else -> throw RuntimeException("Invalid photo type")
        }
    }

    override fun getPhotosLiveData(type: Int): LiveData<List<Photo>> {
        return when (type) {
            Config.TYPE_ALL -> photosDao.getAll()
            Config.TYPE_WITH_FACES -> photosDao.getAll(1)
            Config.TYPE_WITHOUT_FACES -> photosDao.getAllWithoutFaces()
            else -> throw RuntimeException("Invalid photo type")
        }
    }

    override fun savePhotos(photos: List<Photo>) {
        photosDao.insert(photos)
    }

    override fun updatePhotos(photos: List<Photo>) {
        photosDao.update(photos)
    }

    override fun deletePhoto(photo: Photo) {
        photosDao.delete(photo)
    }

    override fun clear() {
        photosDao.deleteAll()
    }
}
