package com.tomergoldst.anyface.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tomergoldst.anyface.model.Photo

@Dao
interface PhotosDao {

    @Query("SELECT * FROM photo")
    fun getAll(): LiveData<List<Photo>>

    @Query("SELECT * FROM photo")
    fun getAllSync(): List<Photo>

    @Query("SELECT * FROM photo WHERE faces_number >= :minFaces")
    fun getAll(minFaces: Int): LiveData<List<Photo>>

    @Query("SELECT * FROM photo WHERE faces_number >= :minFaces")
    fun getAllSync(minFaces: Int): List<Photo>

    @Query("SELECT * FROM photo WHERE faces_number == 0")
    fun getAllWithoutFaces(): LiveData<List<Photo>>

    @Query("SELECT * FROM photo WHERE faces_number == 0")
    fun getAllWithoutFacesSync(): List<Photo>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(photo: Photo)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(photo: List<Photo>)

    @Update
    fun update(photo: Photo)

    @Update
    fun update(photos: List<Photo>)

    @Delete
    fun delete(photo: Photo)

    @Query("DELETE FROM photo")
    fun deleteAll()

}
