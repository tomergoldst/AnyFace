package com.tomergoldst.anyface.workers

import InjectorUtils
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.android.gms.tasks.Tasks
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.tomergoldst.anyface.MyApplication
import com.tomergoldst.anyface.R
import com.tomergoldst.anyface.config.Config
import com.tomergoldst.anyface.ui.MainActivity
import java.io.IOException


const val KEY_RESULT_TOTAL_PHOTOS = "total_photos"
const val KEY_RESULT_TOTAL_PHOTOS_WITH_FACES = "total_photos_with_faces"

class DetectFacesWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params)

{
    companion object {
        val TAG: String = DetectFacesWorker::class.java.simpleName

    }

    override fun doWork(): Result {

        val repository = InjectorUtils.getRepository(context)

        val photos = repository.getPhotosSync(Config.TYPE_ALL)

        val totalPhotos = photos.size
        var totalPhotosWithFaces = 0

        //  face classification options
        val detectionOptions = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .build()

        // face detector
        val detector = FirebaseVision.getInstance().getVisionFaceDetector(detectionOptions)

        // iterate over all photos and run detection for each one
        photos.forEach {
            try {
                val image = FirebaseVisionImage.fromFilePath(context, Uri.parse("file://" + it.path))
                val result = detector.detectInImage(image!!)
                    .addOnSuccessListener { faces ->
                        // Task completed successfully
                        Log.d(TAG, "Detection completed for image ${it.name}")

                        it.facesNumber = faces.size
                        if (it.facesNumber > 0) totalPhotosWithFaces++
                    }
                    .addOnFailureListener { e ->
                        // Task failed with an exception
                        Log.e(TAG, "Detection failed for image ${it.name}", e)
                    }

                // Worker is running on background thread so wait for the detection task to finish
                Tasks.await(result)

            } catch (e: IOException) {
                // if image file wasn't found delete it from repo
                repository.deletePhoto(it)
            }

        }

        // update photos with detection info
        repository.updatePhotos(photos)

        // output results
        val output: Data = workDataOf(
            KEY_RESULT_TOTAL_PHOTOS to totalPhotos,
            KEY_RESULT_TOTAL_PHOTOS_WITH_FACES to totalPhotosWithFaces
        )

        // show notification with results if needed
        val application: MyApplication = context.applicationContext as MyApplication
        if (!application.appActivityLifeCycleCallbacks.isAppInForeground) {
            notifyUser(context, totalPhotos, totalPhotosWithFaces)
        }

        // Indicate success or failure with your return value:
        return Result.success(output)

        // (Returning Result.retry() tells WorkManager to try this task again
        // later; Result.failure() says not to try again.)

    }

    private fun notifyUser(context: Context, totalPhotos: Int, totalPhotosWithFaces: Int) {

        // Create an Intent for the activity you want to start
        val resultIntent = Intent(context, MainActivity::class.java)
        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = NotificationCompat.Builder(context, Config.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setContentTitle(context.getString(R.string.detection_completed_message, totalPhotosWithFaces, totalPhotos))
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Config.NOTIFICATION_ID, builder.build())
    }

}