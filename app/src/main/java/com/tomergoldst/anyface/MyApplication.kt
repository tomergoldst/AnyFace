package com.tomergoldst.anyface

import InjectorUtils
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.FirebaseApp
import com.tomergoldst.anyface.config.Config
import com.tomergoldst.anyface.utils.AppExecutors

class MyApplication : Application() {

    val appExecutors: AppExecutors = InjectorUtils.getAppExecutors()

    val appActivityLifeCycleCallbacks: AppActivityLifecycleCallbacks = AppActivityLifecycleCallbacks()

    override fun onCreate() {
        super.onCreate()

        // Uncomment to debug database
        //Stetho.initializeWithDefaults(applicationContext)

        createNotificationChannel()

        registerActivityLifecycleCallbacks(appActivityLifeCycleCallbacks)

        FirebaseApp.initializeApp(this)

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Config.CHANNEL_ID, name, importance)
            channel.description = description

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)!!
            notificationManager.createNotificationChannel(channel)
        }
    }

}
