package com.test.breedsdogapp.repository

import com.test.breedsdogapp.api.BreedsAPI
import com.test.breedsdogapp.db.AppDataBase
import com.test.breedsdogapp.models.TBreed
import com.test.breedsdogapp.models.TBreedWithTImage
import com.test.breedsdogapp.models.TImage

class MainRepository(private val db: AppDataBase) {

    suspend fun getBreeds() = BreedsAPI.invoke().getBreeds()
    suspend fun getSubBreeds(nameBreed:String) = BreedsAPI.invoke().getSubBreeds(nameBreed)

    suspend fun getBreedImages(breedName:String) = BreedsAPI.invoke().getBreedImages(breedName)
    suspend fun getSubBreedImages(breedName:String, subBreedName:String) = BreedsAPI.invoke().getSubBreedImages(breedName, subBreedName)


    suspend fun checkName(name: String) = db.breedDao()?.loadCheckName(name)
    suspend fun insertFavoriteBreed(breed: TBreed) = db.breedDao()?.insert(breed)
    suspend fun insertFavoriteImage(img: TImage) = db.imageDao()?.insert(img)

    suspend fun deleteBreed(name: String) = db.breedDao()?.delete(name)
    suspend fun deleteImages(url: String) = db.imageDao()?.delete(url)

    fun getImages(item : Long) = db.imageDao()?.loadAllImageBreed(item)

    suspend fun getTBreedWithTImages() = db.breedDao()?.getTBreedWithTImage()
    suspend fun loadAll() = db.breedDao()?.loadAll()

}