package com.test.breedsdogapp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.test.breedsdogapp.models.TBreed
import com.test.breedsdogapp.models.TBreedWithTImage

@Dao
interface BreedDao {

    @Insert
    fun insert(breed: TBreed): Long

    @Update
    fun update(breed: TBreed)

    @Query("DELETE FROM TBreed WHERE name = :name")
    fun delete(name: String)

    @Query("SELECT * FROM TBreed")
    fun loadAll(): MutableList<TBreedWithTImage>

    @Query("SELECT * FROM TBreed")
    fun getTBreedWithTImage(): LiveData<MutableList<TBreedWithTImage>>

    @Query(value = "SELECT id FROM TBreed WHERE name == :name")
    fun loadCheckName(name: String?): Long
}