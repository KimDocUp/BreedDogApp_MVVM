package com.test.breedsdogapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.test.breedsdogapp.models.TImage

@Dao
interface ImageDao {
    @Insert
    fun insert(image: TImage)

    @Update
    fun update(image: TImage)

    @Query( "DELETE FROM TImage WHERE url = :url")
    fun delete(url: String)

    @Query("SELECT * FROM TImage")
    fun loadAll(): MutableList<TImage>

    @Query("SELECT * FROM TImage WHERE breed_id == :breed_id")
    fun loadAllImageBreed(breed_id: Long): MutableList<TImage>

}