package com.test.breedsdogapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.test.breedsdogapp.dao.BreedDao
import com.test.breedsdogapp.dao.ImageDao
import com.test.breedsdogapp.models.TBreed
import com.test.breedsdogapp.models.TImage

@Database(entities = [TBreed::class, TImage::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    abstract fun imageDao(): ImageDao?
    abstract fun breedDao(): BreedDao?

    companion object {
        @Volatile
        private var instance: AppDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }
        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "breed_db.db"
            ).build()
    }
}