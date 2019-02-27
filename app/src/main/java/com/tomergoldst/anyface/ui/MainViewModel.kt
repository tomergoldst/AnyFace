package com.tomergoldst.anyface.ui

import android.app.Application
import android.os.Environment
import androidx.lifecycle.*
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.tomergoldst.anyface.MyApplication
import com.tomergoldst.anyface.config.Config
import com.tomergoldst.anyface.data.DataSource
import com.tomergoldst.anyface.model.Photo
import com.tomergoldst.anyface.workers.DetectFacesWorker
import java.io.File
import java.util.*

class MainViewModel(
    application: Application,
    private val repository: DataSource
) :
    AndroidViewModel(application) {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() =_dataLoading

    private val _detectFacesWorkerDispatched = MutableLiveData<UUID>()
    val detectFacesWorkerDispatched: LiveData<UUID>
        get() =_detectFacesWorkerDispatched

    val allPhotos: LiveData<List<Photo>> = repository.getPhotosLiveData(Config.TYPE_ALL)
    val withFacesPhotos: LiveData<List<Photo>> = repository.getPhotosLiveData(Config.TYPE_WITH_FACES)
    val withoutFacesPhotos: LiveData<List<Photo>> = repository.getPhotosLiveData(Config.TYPE_WITHOUT_FACES)

    private val mApplication: MyApplication = getApplication()

    fun init() {
        _dataLoading.value = true
        loadAllPhotos()

    }

    fun start(){
        val detectFacesWork = OneTimeWorkRequestBuilder<DetectFacesWorker>().build()
        _detectFacesWorkerDispatched.value = detectFacesWork.id
        WorkManager.getInstance().enqueue(detectFacesWork)
    }

    private fun loadAllPhotos() {
        _dataLoading.value = true

        mApplication.appExecutors.diskIO.execute {

            val externalDirectory = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
            val anyVisionDirectory = String.format("%s/%s", externalDirectory, Config.PHOTOS_FOLDER)

            val file = File(anyVisionDirectory)

            val photos: MutableList<Photo> = ArrayList()

            for (fileEntry in file.listFiles()) {
                photos.add(Photo(name = fileEntry.name, path = fileEntry.absolutePath))
            }

            repository.savePhotos(photos)

            _dataLoading.postValue(false)
        }
    }

}