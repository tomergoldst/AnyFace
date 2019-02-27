package com.tomergoldst.anyface.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tomergoldst.anyface.model.Photo

@Database(
    entities = [Photo::class],
    version = 1,
    exportSchema = false
)
internal abstract class DatabaseAccessPoint : RoomDatabase() {

    abstract fun photoDao(): PhotosDao

    companion object {

        private const val DATABASE_NAME = "com.tomergoldst.anyface.db"

        @Volatile
        private var sInstance: DatabaseAccessPoint? = null

        fun getDatabase(context: Context): DatabaseAccessPoint {
            if (sInstance == null) {
                synchronized(DatabaseAccessPoint::class.java) {
                    if (sInstance == null) {
                        sInstance = Room.databaseBuilder(
                            context.applicationContext,
                            DatabaseAccessPoint::class.java,
                            DATABASE_NAME
                        )
                            .build()
                    }
                }
            }
            return sInstance!!
        }
    }

}
